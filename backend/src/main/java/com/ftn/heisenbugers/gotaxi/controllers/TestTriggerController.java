package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.Location;
import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.Route;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


// TODO: Remove before production
@RestController
@RequestMapping("/api/test")
public class TestTriggerController {

    private final UserRepository userRepository;
    private final RideRepository rideRepository;

    public TestTriggerController(UserRepository userRepository, RideRepository rideRepository) {
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
    }

    @GetMapping(value = "trigger")
    public ResponseEntity<String> run() {
        /*Passenger u = (Passenger) userRepository.findById(UUID.fromString("a770919d-3303-45a8-ba06-6de0a97bda93")).get();
        Ride r = rideRepository.findRideById(UUID.fromString("c527273a-ba41-43e2-aa7c-ab78560177ee"));
        r.addPassenger(u);
        rideRepository.save(r);*/

        Ride r = rideRepository.findRideById(UUID.fromString("c527273a-ba41-43e2-aa7c-ab78560177ee"));
        r.setRoute(new Route());
        List<Location> coords = new ArrayList<>();
        coords.add(new Location(45.252223, 19.802843));
        coords.add(new Location(45.242322, 19.796309));
        coords.add(new Location(45.239936, 19.825872));
        coords.add(new Location(45.248063, 19.840472));

        r.getRoute().setPolyline(coords);

        rideRepository.save(r);
        return ResponseEntity.ok("All is done");
    }
}
