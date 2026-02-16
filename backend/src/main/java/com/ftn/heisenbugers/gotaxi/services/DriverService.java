package com.ftn.heisenbugers.gotaxi.services;

import com.ftn.heisenbugers.gotaxi.models.*;
import com.ftn.heisenbugers.gotaxi.models.dtos.DriverRideHistoryDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.LocationDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.PassengerInfoDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.TrafficViolationDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.RideSort;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import com.ftn.heisenbugers.gotaxi.repositories.DriverRepository;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.repositories.TrafficViolationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DriverService {

    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final TrafficViolationRepository trafficViolationRepository;

    public DriverService(RideRepository rideRepository, DriverRepository driverRepository, TrafficViolationRepository trafficViolationRepository) {
        this.rideRepository = rideRepository;
        this.driverRepository = driverRepository;
        this.trafficViolationRepository = trafficViolationRepository;
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
            List<TrafficViolation> trafficViolations = trafficViolationRepository.getAllByRide(r);

            populateDto(r, dto, trafficViolations);
            rideHistoryDTOS.add(dto);
        }


        return rideHistoryDTOS;
    }

    public List<Driver> findActiveDrivers() {
        return driverRepository.findByWorkingTrue();
    }

    public boolean canAcceptRide(Driver driver, int estimatedTime) {
        return driver.getActiveHoursLast24h() + (estimatedTime / 60) < 8;
    }

    public boolean endsRideWithin(Driver driver, int minutes) {

        List<Ride> activeRides =
                rideRepository.findActiveRidesByDriver(driver.getId());

        if (activeRides.isEmpty()) {
            return false;
        }

        if (activeRides.size() > 1) {
            return false;
        }

        Ride ride = activeRides.get(0);

        if (ride.getStartedAt() == null) {
            return false;
        }

        LocalDateTime expectedEnd =
                ride.getStartedAt()
                        .plusMinutes(ride.getRoute().getEstimatedTimeMin());

        return expectedEnd.isBefore(LocalDateTime.now().plusMinutes(minutes));
    }

    public boolean vehicleMatchesRequest(
            Driver driver,
            boolean petTransport,
            boolean babyTransport,
            VehicleType requestedType
    ) {
        Vehicle vehicle = driver.getVehicle();

        if (vehicle == null) return false;

        if (vehicle.getType() != requestedType) {
            return false;
        }

        if (petTransport && !vehicle.isPetTransport()) {
            return false;
        }

        if (babyTransport && !vehicle.isBabyTransport()) {
            return false;
        }

        return true;
    }

    private static void populateDto(Ride r, DriverRideHistoryDTO dto, List<TrafficViolation> trafficViolations) {
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
        dto.setPanicTriggered(r.getPanicEvent() != null);
        dto.setCanceledBy(r.getCanceledBy());
        dto.setStartedAt(r.getStartedAt());
        try {
            List<Location> locations = r.getRoute().getStops();
            List<LocationDTO> outputLocations = new ArrayList<>();
            for (Location l : locations) {
                LocationDTO locationDTO = new LocationDTO();
                populateDto(l, locationDTO);
                outputLocations.add(locationDTO);
            }
            dto.setRoute(outputLocations);
        } catch (NullPointerException e) {
            dto.setRoute(new ArrayList<>());
        }
        User mainPassenger = r.getRoute().getUser();
        PassengerInfoDTO mainPassengerDTO = new PassengerInfoDTO();
        populateDto(mainPassenger, mainPassengerDTO);
        dto.addPassenger(mainPassengerDTO);

        List<User> passengers = r.getPassengers();
        for (User p : passengers) {
            PassengerInfoDTO passengerDTO = new PassengerInfoDTO();
            populateDto(p, passengerDTO);
            dto.addPassenger(passengerDTO);
        }

        List<TrafficViolationDTO> trafficViolationDTOS = new ArrayList<>();
        for (TrafficViolation tv : trafficViolations) {
            TrafficViolationDTO trafficViolationDTO = new TrafficViolationDTO();
            populateDto(tv, trafficViolationDTO);
            trafficViolationDTOS.add(trafficViolationDTO);
        }
        dto.setTrafficViolations(trafficViolationDTOS);
    }

    private static void populateDto(User p, PassengerInfoDTO passengerDTO) {
        passengerDTO.setPassengerId(p.getId());

        if (p.getFirstName() == null || p.getFirstName().isEmpty()) {
            passengerDTO.setFirstName(p.getEmail());
        } else {
            passengerDTO.setFirstName(p.getFirstName());
            passengerDTO.setLastName(p.getLastName());
        }
        //passengerDTO.setProfileImageUrl(p.getProfileImageUrl());
        //new image upload


    }

    private static void populateDto(Location l, LocationDTO dto) {
        dto.setAddress(l.getAddress());
        dto.setLongitude(l.getLongitude());
        dto.setLatitude(l.getLatitude());
    }

    private static void populateDto(TrafficViolation tv, TrafficViolationDTO dto) {
        dto.setDescription(tv.getDescription());
        dto.setTitle(tv.getTitle());
    }
}
