package com.ftn.heisenbugers.gotaxi.services;

import com.ftn.heisenbugers.gotaxi.models.*;
import com.ftn.heisenbugers.gotaxi.models.dtos.DriverDto;
import com.ftn.heisenbugers.gotaxi.models.dtos.LocationDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.RideDTO;
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

    public RideService(RideRepository rideRepository, UserRepository userRepository,
                       TrafficViolationRepository violationRepository, RatingRepository ratingRepository) {
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

    public RideDTO getRide(UUID rideId) {
        Ride r = rideRepository.findRideById(rideId);
        Driver d = r.getDriver();
        return new RideDTO(
                r.getId().toString(),
                new DriverDto(
                        d.getFirstName(),
                        d.getLastName()
                ),
                r.getRoute().getStops().stream().map(LocationDTO::new).toList(),
                new LocationDTO(r.getStart()),
                new LocationDTO(r.getEnd()),
                r.getPrice()
        );
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
                    .map(location -> new LocationDTO(location.getLatitude(),
                            location.getLongitude(), location.getAddress()))
                    .toList();
        }

        DriverDto driverDto = new DriverDto(driver.getFirstName(), driver.getLastName());
        Location startLocation = ride.getStart();
        LocationDTO startLocationDTO = new LocationDTO(startLocation.getLatitude(),
                startLocation.getLongitude(), startLocation.getAddress());

        Location endLocation = ride.getEnd();
        LocationDTO endLocationDTO = new LocationDTO(endLocation.getLatitude(),
                endLocation.getLongitude(), endLocation.getAddress());

        return new RideTrackingDTO(
                rideId,
                driverDto,
                currentLocation.getLatitude(),
                currentLocation.getLongitude(),
                0,
                routeDTOs,
                startLocationDTO,
                endLocationDTO
        );

    }

    public boolean report(UUID rideId, UUID reporterId, String title, String desc) {
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
        trafficViolation.setTitle(title);
        trafficViolation.setDescription(desc);
        trafficViolation.setCreatedBy(reporter);
        trafficViolation.setLastModifiedBy(reporter);

        violationRepository.save(trafficViolation);
        return true;
    }

    public boolean finish(UUID rideId, UUID driverId) {
        Driver driver = (Driver) userRepository.findById(driverId).get();
        Ride ride = rideRepository.findById(rideId).get();
        Driver rideDriver = ride.getDriver();
        if (ride.getStatus() != RideStatus.ONGOING || driver != rideDriver) {
            return false;
        }

        ride.setEndedAt(LocalDateTime.now());
        ride.setStatus(RideStatus.FINISHED);
        ride.setLastModifiedBy(driver);
        rideRepository.save(ride);
        return true;
    }

    public boolean rate(UUID rideId, UUID raterId, int driverScore, int vehicleScore, String comment) {
        Ride ride = rideRepository.findById(rideId).get();
        User rater = userRepository.findById(raterId).get();

        // If already rated
        if (ratingRepository.findByRaterAndRide(rater, ride).isPresent()) {
            return false;
        }

        // If too old to rate
        if (!isInLastNDays(ride, 3)) {
            return false;
        }

        Rating rating = new Rating();

        rating.setRide(ride);
        rating.setRater(rater);
        rating.setDriverScore(driverScore);
        rating.setVehicleScore(vehicleScore);
        rating.setComment(comment);

        rating.setLastModifiedBy(rater);
        ratingRepository.save(rating);
        return true;
    }

    private boolean isInLastNDays(Ride r, long n) {
        return r.getEndedAt().isAfter(LocalDateTime.now().minusDays(n));
    }
}
