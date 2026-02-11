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

        User u = userRepository.findById(UUID.fromString("32e8c49a-8a81-456b-ade6-5da9cd87f559")).get();

        notificationService.notifyUser(u, "Live Notification");
        return ResponseEntity.ok("Success");
    }
}
