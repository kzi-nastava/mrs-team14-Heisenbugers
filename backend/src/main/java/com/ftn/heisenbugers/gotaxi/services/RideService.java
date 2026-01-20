package com.ftn.heisenbugers.gotaxi.services;

import com.ftn.heisenbugers.gotaxi.models.*;
import com.ftn.heisenbugers.gotaxi.models.dtos.DriverDto;
import com.ftn.heisenbugers.gotaxi.models.dtos.LocationDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.RideTrackingDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.repositories.RatingRepository;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.repositories.TrafficViolationRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RideService {
    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final TrafficViolationRepository violationRepository;
    private final RatingRepository ratingRepository;

    public RideService(RideRepository rideRepository, UserRepository userRepository, TrafficViolationRepository violationRepository, RatingRepository ratingRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.violationRepository = violationRepository;
        this.ratingRepository = ratingRepository;
    }

    public List<RideTrackingDTO> getAll() {
        List<Ride> rides = rideRepository.findAll();

        return rides.stream()
                .map(ride -> {
                    RideTrackingDTO dto = new RideTrackingDTO();
                    dto.setRideId(ride.getId());
                    Driver driver = ride.getDriver();
                    DriverDto driverDto = new DriverDto(driver.getFirstName(), driver.getLastName());

                    dto.setVehicleLatitude(driver.getLocation().getLatitude());
                    dto.setVehicleLongitude(driver.getLocation().getLongitude());
                    dto.setDriver(driverDto);
                    return dto;
                })
                .toList();
    }

    public RideTrackingDTO getRideTrackingById(UUID rideId) {
        Optional<Ride> rideOpt = rideRepository.findById(rideId);
        Ride ride = rideOpt.orElse(null);

        if (ride == null || ride.getStatus() != RideStatus.ONGOING) {
            return null;
        }

        // Get current vehicle location
        Driver driver = ride.getDriver();
        Location currentLocation = driver.getLocation();

        // Calculate remaining time based on route and current position
        Route route = ride.getRoute();
        List<LocationDTO> routeDTOs = new ArrayList<>();
        if (route != null) {
            // Convert route locations to DTOs
            routeDTOs = route.getStops().stream()
                    .map(location -> new LocationDTO(location.getLatitude(), location.getLongitude(), location.getAddress()))
                    .toList();
        }

        DriverDto driverDto = new DriverDto(driver.getFirstName(), driver.getLastName());

        return new RideTrackingDTO(
                rideId,
                driverDto,
                currentLocation.getLatitude(),
                currentLocation.getLongitude(),
                0,
                routeDTOs
        );

    }

    public boolean report(UUID rideId, UUID reporterId, String desc) {
        Optional<Ride> rideOpt = rideRepository.findById(rideId);
        Ride ride = rideOpt.orElse(null);

        if (ride == null || ride.getStatus() != RideStatus.ONGOING) {
            return false;
        }

        TrafficViolation trafficViolation = new TrafficViolation();
        Optional<User> reporterOpt = userRepository.findById(reporterId);
        User reporter = reporterOpt.orElse(null);
        if (reporter == null) {
            return false;
        }
        trafficViolation.setReporter(reporter);
        trafficViolation.setRide(ride);
        trafficViolation.setDescription(desc);
        trafficViolation.setCreatedBy(reporter);
        trafficViolation.setLastModifiedBy(reporter);
        violationRepository.save(trafficViolation);
        return true;
    }

    public boolean finish(UUID rideId) {
        Ride ride = rideRepository.findById(rideId).get();
        if (ride.getStatus() != RideStatus.ONGOING) {
            return false;
        }

        ride.setEndedAt(LocalDateTime.now());
        ride.setStatus(RideStatus.FINISHED);
        rideRepository.save(ride);
        return true;
    }

    public void rate(UUID rideId, int driverScore, int vehicleScore, String comment) {
        Ride ride = rideRepository.findById(rideId).get();

        if (!isInLastNDays(ride, 3)) {
            return;
        }

        Rating rating = new Rating();
        rating.setRide(ride);
        rating.setDriverScore(driverScore);
        rating.setVehicleScore(vehicleScore);

        rating.setComment(comment);
        ratingRepository.save(rating);
    }

    private boolean isInLastNDays(Ride r, long n) {
        return r.getEndedAt().isAfter(LocalDateTime.now().minusDays(n));
    }
}
