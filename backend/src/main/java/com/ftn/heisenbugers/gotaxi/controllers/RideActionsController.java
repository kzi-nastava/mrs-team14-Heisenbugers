package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.dtos.CancelRideRequestDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.MessageResponse;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/rides")
public class RideActionsController {

    @Autowired
    private RideRepository rideRepository;

    // Cancel ride
    @PostMapping("/{rideId}/cancel")
    public ResponseEntity<?> cancelRide(@PathVariable UUID rideId,
                                        @RequestBody CancelRideRequestDTO request) {

        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Ride not found."));
        }

        // add time/role checks
        ride.setStatus(RideStatus.CANCELED);
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
