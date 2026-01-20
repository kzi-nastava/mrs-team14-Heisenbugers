package com.ftn.heisenbugers.gotaxi.services;

import com.ftn.heisenbugers.gotaxi.models.Passenger;
import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.dtos.DriverRideHistoryDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.PassengerInfoDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.RideSort;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DriverService {

    private final RideRepository rideRepository;

    public DriverService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    public List<DriverRideHistoryDTO> getDriverHistory(UUID driverId, LocalDate startDate, LocalDate endDate,
                                                       RideSort sortBy, String direction) {
        List<Ride> rides;

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy.getProperty()).descending()
                : Sort.by(sortBy.getProperty()).ascending();


        if (startDate != null && endDate == null) {
            rides = rideRepository.findByDriverIdAndStartedAtAfter(driverId, startDate.atStartOfDay(), sort);
        } else if (startDate == null && endDate != null) {
            rides = rideRepository.findByDriverIdAndStartedAtBefore(driverId,
                    endDate.plusDays(1).atStartOfDay(),
                    sort);
        } else if (startDate != null) {
            rides = rideRepository.findByDriverIdAndStartedAtBetween(driverId,
                    startDate.atStartOfDay(),
                    endDate.plusDays(1).atStartOfDay(),
                    sort);
        } else {
            rides = rideRepository.findByDriverId(driverId, sort);
        }
        List<DriverRideHistoryDTO> rideHistoryDTOS = new ArrayList<>();
        for (Ride r : rides) {
            DriverRideHistoryDTO dto = new DriverRideHistoryDTO();
            PopulateDto(r, dto);
            rideHistoryDTOS.add(dto);
        }

        return rideHistoryDTOS;
    }

    private static void PopulateDto(Ride r, DriverRideHistoryDTO dto) {
        dto.setRideId(r.getId());
        dto.setCanceled(r.isCanceled());
        dto.setPrice(r.getPrice());
        dto.setEndedAt(r.getEndedAt());
        try {
            dto.setEndAddress(r.getEnd().getAddress());
        } catch (NullPointerException e) {
            dto.setEndAddress("");
        }
        try {
            dto.setStartAddress(r.getStart().getAddress());
        } catch (NullPointerException e) {
            dto.setStartAddress("");
        }
        dto.setPanicTriggered(r.getPanicEvent() == null);
        dto.setCanceledBy(r.getCanceledBy());
        dto.setStartedAt(r.getStartedAt());

        List<Passenger> passengers = r.getPassengers();
        for (Passenger p : passengers) {
            PassengerInfoDTO passengerDTO = new PassengerInfoDTO();
            PopulateDto(p, passengerDTO);
            dto.addPassenger(passengerDTO);
        }
    }

    private static void PopulateDto(Passenger p, PassengerInfoDTO passengerDTO) {
        passengerDTO.setPassengerId(p.getId());
        passengerDTO.setFirstName(p.getFirstName());
        passengerDTO.setLastName(p.getLastName());
        passengerDTO.setProfileImageUrl(p.getProfileImageUrl());
    }
}
