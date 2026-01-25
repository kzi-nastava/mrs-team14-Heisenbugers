package com.ftn.heisenbugers.gotaxi.controllers;


import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.dtos.CreateRideDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.CreatedRideDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.DriverDto;
import com.ftn.heisenbugers.gotaxi.models.dtos.StartedRideDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/rides")
public class CreateRideController {
    private final RideRepository rideRepository;

    public CreateRideController(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedRideDTO> createRide(@RequestBody CreateRideDTO request) {

        Ride ride = new Ride();
        ride.setStatus(RideStatus.REQUESTED);
        ride.setScheduledAt(null);
        ride.setCanceled(false);

        // TODO: later bind start/end/route/passengers/vehicleType etc.
        // ride.setRoute(...)
        // ride.setPassengers(...)
        // ride.setStart(...)
        // ride.setEnd(...)
        // ride.setDriver(...)

        ride = rideRepository.save(ride);


        CreatedRideDTO response = new CreatedRideDTO();
        response.setId(ride.getId());
        response.setStatus(RideStatus.REQUESTED);
        response.setDriver(new DriverDto("Driver", "Driver"));
        response.setRoute(request.getRoute());
        response.setPassengers(request.getPassengers());
        response.setPetTransport(request.isPetTransport());
        response.setBabyTransport(request.isBabyTransport());
        response.setVehicleType(request.getVehicleType());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
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
