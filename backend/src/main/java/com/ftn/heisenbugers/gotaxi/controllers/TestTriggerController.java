package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import com.ftn.heisenbugers.gotaxi.services.NotificationService;
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

    @GetMapping(value = "trigger")
    public ResponseEntity<String> run() {

        User u = userRepository.findById(UUID.fromString("62833df4-17f9-4695-a576-dd0e883dc8d7")).get();

        notificationService.notifyUser(u, "Live Notification");
        return ResponseEntity.ok("Success");
    }
}
