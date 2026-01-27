package com.ftn.heisenbugers.gotaxi.controllers;


import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.dtos.CreateRideDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.CreatedRideDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.DriverDto;
import com.ftn.heisenbugers.gotaxi.models.dtos.StartedRideDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.services.RideService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/rides")
public class CreateRideController {
    private final RideRepository rideRepository;
    private final RideService rideService;

    public CreateRideController(RideRepository rideRepository, RideService rideService) {
        this.rideRepository = rideRepository;
        this.rideService = rideService;
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedRideDTO> createRide(@RequestBody CreateRideDTO request) throws InvalidUserType {

        CreatedRideDTO ride = rideService.addRide(request);

        return new ResponseEntity<>(ride, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}/start",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StartedRideDTO> startRide(@PathVariable Long id) {

        StartedRideDTO response = new StartedRideDTO();
        response.setRideId(id);
        response.setStatus(RideStatus.ONGOING);
        response.setStartedAt(LocalDateTime.now());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
