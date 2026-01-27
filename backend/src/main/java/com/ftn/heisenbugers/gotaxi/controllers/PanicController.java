package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.*;
import com.ftn.heisenbugers.gotaxi.models.dtos.MessageResponse;
import com.ftn.heisenbugers.gotaxi.models.dtos.PanicRequestDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.repositories.NotificationRepository;
import com.ftn.heisenbugers.gotaxi.repositories.PanicEventRepository;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/rides")
public class PanicController {

    private final RideRepository rideRepository;
    private final PanicEventRepository panicRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;


    public PanicController(RideRepository rideRepository,
                           PanicEventRepository panicRepository,
                           UserRepository userRepository,
                           NotificationRepository notificationRepository) {
        this.rideRepository = rideRepository;
        this.panicRepository = panicRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @PostMapping("/{rideId}/panic")
    public ResponseEntity<?> panic(@PathVariable UUID rideId,
                                   @RequestBody(required = false) PanicRequestDTO req) throws InvalidUserType {

        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Ride not found."));
        }

        if (ride.getStatus() != RideStatus.ONGOING) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("Panic is allowed only during ONGOING ride."));
        }

        User current = AuthContextService.getCurrentUser();

        boolean isDriver = (current instanceof Driver)
                && ride.getDriver() != null
                && ride.getDriver().getId().equals(current.getId());

        boolean isPassenger = (current instanceof Passenger)
                && ride.getPassengers() != null
                && ride.getPassengers().stream().anyMatch(p -> p.getId().equals(current.getId()));

        if (!isDriver && !isPassenger) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("You are not a participant of this ride."));
        }


        var existing = panicRepository.findFirstByRideIdAndResolvedFalseOrderByCreatedAtDesc(rideId);
        if (existing.isPresent()) {
            return ResponseEntity.ok(new MessageResponse("Panic already active."));
        }


        String msg = (req != null && req.getMessage() != null && !req.getMessage().trim().isEmpty())
                ? req.getMessage().trim()
                : "PANIC button pressed!";

        PanicEvent pe = PanicEvent.builder()
                .resolved(false)
                .ride(ride)
                .handledBy(null)
                .build();

        panicRepository.save(pe);




        List<User> admins = userRepository.findAll().stream()
                .filter(u -> u instanceof Administrator)
                .toList();

        for (User a : admins) {
            Notification n = Notification.builder()
                    .message("PANIC for ride " + rideId + ": " + msg)
                    .read(false)
                    .readAt(null)
                    .user(a)
                    .ride(ride)
                    .build();
            notificationRepository.save(n);
        }

        return ResponseEntity.ok(new MessageResponse("Panic created."));
    }
}
