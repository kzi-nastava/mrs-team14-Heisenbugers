package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Route;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import com.ftn.heisenbugers.gotaxi.services.NotificationService;
import com.ftn.heisenbugers.gotaxi.utils.DrivingSimulator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


// TODO: Remove before production
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestTriggerController {

    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final NotificationService notificationService;
    private final DrivingSimulator drivingSimulator;

    @GetMapping(value = "trigger")
    public ResponseEntity<String> run() throws Exception {
        Driver d = (Driver) userRepository.findById(UUID.fromString("62833df4-17f9-4695-a576-dd0e883dc8d7")).get();

        Route r = rideRepository.findRideById(UUID.fromString("a8bb0c99-362d-4892-b0d4-dccb6f146c36")).getRoute();
        drivingSimulator.driveRoute(r, d.getId());

        return ResponseEntity.ok("");
    }
}
