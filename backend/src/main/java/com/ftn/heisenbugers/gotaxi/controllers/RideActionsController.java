package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Passenger;
import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.dtos.CancelRideRequestDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.MessageResponse;
import com.ftn.heisenbugers.gotaxi.models.dtos.StopRideRequestDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/rides")
public class RideActionsController {

    @Autowired
    private RideRepository rideRepository;

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
        User currentUser = AuthContextService.getCurrentUser(); // раз Driver наследуется от User — ок

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
                                      @RequestBody(required = false) StopRideRequestDTO request) {

        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Ride not found."));
        }

        if (ride.getStatus() != RideStatus.ONGOING) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("Ride is not ongoing."));
        }


        ride.setStatus(RideStatus.FINISHED);
        ride.setEndedAt(LocalDateTime.now());


        rideRepository.save(ride);

        return ResponseEntity.ok(new MessageResponse("Ride stopped and finished."));
    }

}
