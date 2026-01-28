package com.ftn.heisenbugers.gotaxi.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.*;
import com.ftn.heisenbugers.gotaxi.models.dtos.CancelRideRequestDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.MessageResponse;
import com.ftn.heisenbugers.gotaxi.models.dtos.StopRideRequestDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.repositories.LocationRepository;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/rides")
public class RideActionsController {

    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private LocationRepository locationRepository;

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    // Cancel ride
    @PostMapping("/{rideId}/cancel")
    public ResponseEntity<?> cancelRide(@PathVariable UUID rideId,
                                        @RequestBody CancelRideRequestDTO request) throws InvalidUserType {

        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Ride not found."));
        }

        //if canceled or finish
        if (ride.getStatus() == RideStatus.CANCELED || ride.getStatus() == RideStatus.FINISHED) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("Ride cannot be canceled in current status."));
        }
        //if in process
        if (ride.getStatus() == RideStatus.ONGOING) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("Ride is ongoing. Cannot cancel."));
        }

        String reason = request != null ? request.getReason() : null;
        User currentUser = AuthContextService.getCurrentUser();

        boolean isDriver = (currentUser instanceof Driver);
        boolean isPassenger = (currentUser instanceof Passenger);

        if (!isDriver && !isPassenger) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Only driver or passenger can cancel a ride."));
        }


        if (isDriver) {
            Driver driver = (Driver) currentUser;


            if (ride.getDriver() == null || !ride.getDriver().getId().equals(driver.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("You are not the driver of this ride."));
            }


            if (ride.getStatus() != RideStatus.ASSIGNED) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new MessageResponse("Driver can cancel only ASSIGNED rides."));
            }


            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Cancel reason is required for driver."));
            }
        }

        if (isPassenger) {
            Passenger passenger = (Passenger) currentUser;


            UUID passengerId = passenger.getId();
            boolean isPassengerOfRide = ride.getPassengers() != null
                    && ride.getPassengers().stream().anyMatch(p -> p.getId().equals(passengerId));

            if (!isPassengerOfRide) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("You are not a passenger of this ride."));
            }


            if (ride.getScheduledAt() == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new MessageResponse("This ride cannot be canceled by passenger (no scheduled time)."));
            }

            LocalDateTime latestCancel = ride.getScheduledAt().minusMinutes(10);
            if (LocalDateTime.now().isAfter(latestCancel)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new MessageResponse("You can cancel only up to 10 minutes before start."));
            }


        }

        ride.setStatus(RideStatus.CANCELED);
        ride.setCanceled(true);
        ride.setCanceledBy(currentUser);
        ride.setCancelReason(reason != null ? reason.trim() : null);
        ride.setCanceledAt(LocalDateTime.now());

        rideRepository.save(ride);

        return ResponseEntity.ok(new MessageResponse("Ride canceled."));

    }

    // Stop ride
    @PostMapping("/{rideId}/stop")
    public ResponseEntity<?> stopRide(@PathVariable UUID rideId,
                                      @RequestBody(required = false) StopRideRequestDTO request) throws InvalidUserType {

        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Ride not found."));
        }

        if (ride.getStatus() != RideStatus.ONGOING) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("Ride is not ongoing."));
        }


        //
        Driver currentDriver = AuthContextService.getCurrentDriver();
        if (ride.getDriver() == null || !ride.getDriver().getId().equals(currentDriver.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Only ride driver can stop the ride."));
        }

        if (request == null || request.getLatitude() == null || request.getLongitude() == null) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Stop location (latitude/longitude) is required."));
        }
/*
        //stop point
        Double lat = request != null ? request.getLatitude() : null;
        Double lon = request != null ? request.getLongitude() : null;
        String addr = request != null ? request.getAddress() : null;

        if (lat == null || lon == null) {
            // fallback: берём location водителя
            if (currentDriver.getLocation() == null) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Stop location is missing (no coords in request and driver has no location)."));
            }
            lat = currentDriver.getLocation().getLatitude();
            lon = currentDriver.getLocation().getLongitude();
            if (addr == null || addr.isBlank()) {
                addr = "Stopped location";
            }
        } else {
            if (addr == null || addr.isBlank()) {
                addr = "Stopped location";
            }
        }*/
        if (ride.getStart() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("Ride has no start location."));
        }

        Location oldEnd = ride.getEnd();
        String addr = (request.getAddress() != null && !request.getAddress().trim().isEmpty())
                ? request.getAddress().trim()
                : ("Stopped at: " + request.getLatitude() + ", " + request.getLongitude());

        //save new distance
        Location stopLocation = Location.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(addr)
                .build();

        stopLocation = locationRepository.save(stopLocation);






        ride.setEnd(stopLocation);
        ride.setEndedAt(LocalDateTime.now());
        ride.setStatus(RideStatus.FINISHED);
        rideRepository.save(ride);

        double newPrice = recalcPriceOnStop(ride, oldEnd, stopLocation);
        ride.setPrice(newPrice);
        rideRepository.save(ride);



        //for log
        return ResponseEntity.ok(Map.of(
                "message", "Ride stopped and finished.",
                "rideId", ride.getId(),
                "endedAt", ride.getEndedAt(),
                "newDestination", Map.of(
                        "latitude", stopLocation.getLatitude(),
                        "longitude", stopLocation.getLongitude(),
                        "address", stopLocation.getAddress()
                ),
                "price", ride.getPrice()
        ));

        //return ResponseEntity.ok(new MessageResponse("Ride stopped and finished."));
    }

    private static class OsrmResult {
        final double distanceKm;
        final int timeMin;
        OsrmResult(double distanceKm, int timeMin) {
            this.distanceKm = distanceKm;
            this.timeMin = timeMin;
        }
    }


    //helper func-s
    private double recalcPriceOnStop(Ride ride, Location oldEnd, Location stop) {
        double oldPrice = ride.getPrice();
        if (oldPrice <= 0) return oldPrice;

        Location start = ride.getStart();
        if (start == null || oldEnd == null) return oldPrice;

        double fullKm = haversineKm(start.getLatitude(), start.getLongitude(),
                oldEnd.getLatitude(), oldEnd.getLongitude());

        double stopKm = haversineKm(start.getLatitude(), start.getLongitude(),
                stop.getLatitude(), stop.getLongitude());

        if (fullKm <= 0.1) return oldPrice;

        double ratio = stopKm / fullKm;

        
        ratio = Math.max(0.10, Math.min(ratio, 1.00));


        double newPrice = oldPrice * ratio;
        return Math.round(newPrice * 100.0) / 100.0;
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

}
