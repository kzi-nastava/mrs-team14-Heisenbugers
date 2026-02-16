package com.ftn.heisenbugers.gotaxi.services;

import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.dtos.DailyItemDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.RideAnalyticsResponseDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.TotalsDTO;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RideAnalyticsService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public RideAnalyticsResponseDTO getAnalytics(
            LocalDate startDate,
            LocalDate endDate,
            String role,
            UUID userId,
            boolean aggregate
    ) {

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Ride> rides;

        if (aggregate) {
            rides = rideRepository.findAllCompletedBetween(start, end);
        } else if ("ADMIN".equals(role) && userId != null){
            User user = userRepository.findUserById(userId);
            if (user instanceof Driver){
                rides = rideRepository.findDriverRidesBetween(userId, start, end);
            }else{
                rides = rideRepository.findOrderedRidesBetween(userId, start, end);
            }
        } else if ("DRIVER".equals(role) && userId != null) {
            rides = rideRepository.findDriverRidesBetween(userId, start, end);
        } else if ("PASSENGER".equals(role) && userId != null) {
            rides = rideRepository.findOrderedRidesBetween(userId, start, end);
        } else {
            rides = rideRepository.findOrderedRidesBetween(userId, start, end);
        }

        Map<LocalDate, List<Ride>> grouped =
                rides.stream()
                        .collect(Collectors.groupingBy(r -> r.getEndedAt().toLocalDate()));

        List<DailyItemDTO> daily = new ArrayList<>();

        LocalDate cursor = startDate;

        while (!cursor.isAfter(endDate)) {

            List<Ride> dayRides = grouped.getOrDefault(cursor, List.of());

            long count = dayRides.size();

            double km = dayRides.stream()
                    .mapToDouble(r -> r.getRoute() != null ? r.getRoute().getDistanceKm() : 0)
                    .sum();

            double money = dayRides.stream()
                    .mapToDouble(Ride::getPrice)
                    .sum();

            daily.add(new DailyItemDTO(
                    cursor.toString(),
                    count,
                    km,
                    money
            ));

            cursor = cursor.plusDays(1);
        }

        long totalRides = daily.stream().mapToLong(DailyItemDTO::getRides).sum();
        double totalKm = daily.stream().mapToDouble(DailyItemDTO::getKilometers).sum();
        double totalMoney = daily.stream().mapToDouble(DailyItemDTO::getMoney).sum();

        return RideAnalyticsResponseDTO.builder()
                .daily(daily)
                .totals(new TotalsDTO(totalRides, totalKm, totalMoney))
                .build();
    }
}