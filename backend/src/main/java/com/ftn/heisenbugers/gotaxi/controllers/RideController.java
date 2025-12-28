package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.*;
import com.ftn.heisenbugers.gotaxi.models.dtos.DriverDto;
import com.ftn.heisenbugers.gotaxi.models.dtos.LocationDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.RideTrackingDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.repositories.RatingRepository;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.repositories.TrafficViolationRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final TrafficViolationRepository violationRepository;
    private final RatingRepository ratingRepository;

    public RideController(RideRepository rideRepository, UserRepository userRepository,
                          TrafficViolationRepository violationRepository, RatingRepository ratingRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.violationRepository = violationRepository;
        this.ratingRepository = ratingRepository;
    }

    @GetMapping("")
    public ResponseEntity<List<RideTrackingDTO>> getRideTracking() {
        List<Ride> rides = rideRepository.findAll();

        List<RideTrackingDTO> trackingDTOs = rides.stream()
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
                .collect(Collectors.toList());

        return ResponseEntity.ok(trackingDTOs);
    }


    @GetMapping("/{rideId}/tracking")
    public ResponseEntity<RideTrackingDTO> getRideTracking(@PathVariable UUID rideId) {
        Optional<Ride> rideOpt = rideRepository.findById(rideId);
        Ride ride = rideOpt.orElse(null);

        if (ride == null || ride.getStatus() != RideStatus.ONGOING) {
            return ResponseEntity.notFound().build();
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

        RideTrackingDTO trackingDTO = new RideTrackingDTO(
                rideId,
                driverDto,
                currentLocation.getLatitude(),
                currentLocation.getLongitude(),
                0,
                routeDTOs
        );

        return ResponseEntity.ok(trackingDTO);
    }

    @PostMapping("/{rideId}/report")
    public ResponseEntity<Object> reportDriver(@PathVariable UUID rideId, @RequestBody Map<String, Object> body) {
        Optional<Ride> rideOpt = rideRepository.findById(rideId);
        Ride ride = rideOpt.orElse(null);

        if (ride == null || ride.getStatus() != RideStatus.ONGOING) {
            return ResponseEntity.notFound().build();
        }

        TrafficViolation trafficViolation = new TrafficViolation();
        Optional<User> reporterOpt = userRepository.findById(UUID.fromString((String) body.get("reporterId")));
        User reporter = reporterOpt.orElse(null);
        if (reporter == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "User reporter not found"));
        }
        trafficViolation.setReporter(reporter);
        trafficViolation.setRide(ride);
        trafficViolation.setDescription((String) body.get("description"));
        trafficViolation.setCreatedBy(reporter);
        trafficViolation.setLastModifiedBy(reporter);
        violationRepository.save(trafficViolation);
        return ResponseEntity.ok().build();

    }

    @PostMapping("/{rideId}/finish")
    public ResponseEntity<Object> finishRide(@PathVariable UUID rideId) {
        Ride ride = rideRepository.findById(rideId).get();
        if (ride.getStatus() != RideStatus.ONGOING) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Ride already finished")
            );
        }

        ride.setEndedAt(LocalDateTime.now());
        ride.setStatus(RideStatus.FINISHED);
        rideRepository.save(ride);


        return ResponseEntity.ok().body(
                Map.of("message", "Ride finished successfully")
        );
    }

    @PostMapping("/{rideId}/rate")
    public ResponseEntity<Object> rateDriver(@PathVariable UUID rideId, @RequestBody Map<String, Object> body) {
        Ride ride = rideRepository.findById(rideId).get();
        Driver driver = ride.getDriver();

        Rating rating = new Rating();
        rating.setRide(ride);
        try {
            rating.setDriverScore(Integer.parseInt((String) body.get("driverScore")));
            rating.setVehicleScore(Integer.parseInt((String) body.get("vehicleScore")));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Rating must be integers"));
        }
        rating.setComment((String) body.get("comment"));
        ratingRepository.save(rating);
        return ResponseEntity.ok()
                .body(Map.of("message", "Ride successfully rated"));
    }

    @ExceptionHandler(ClassCastException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleCastFailure(ClassCastException ex) {
        return Map.of("error", "Invalid field type", "details", ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoSuchElement(NoSuchElementException ex) {
        return Map.of("error", "Resource not found", "details", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleCastFailure(MethodArgumentTypeMismatchException ex) {
        return Map.of("error", "Invalid field type", "details", ex.getMessage());
    }


}
