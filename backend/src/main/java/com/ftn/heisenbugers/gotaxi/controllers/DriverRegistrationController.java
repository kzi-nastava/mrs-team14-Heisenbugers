package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.dtos.CreateDriverDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.CreatedDriverDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.CreatedVehicleDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.SetDriverPasswordDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drivers")
public class DriverRegistrationController {

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CreatedDriverDTO> createDriver(
            @RequestBody CreateDriverDTO request) {

        CreatedVehicleDTO vehicle = new CreatedVehicleDTO();
        vehicle.setId(1L);
        vehicle.setModel(request.getVehicle().getVehicleModel());
        vehicle.setType(request.getVehicle().getVehicleType());
        vehicle.setLicensePlate(request.getVehicle().getLicensePlate());
        vehicle.setSeatCount(request.getVehicle().getSeatCount());
        vehicle.setBabyTransport(request.getVehicle().isBabyTransport());
        vehicle.setPetTransport(request.getVehicle().isPetTransport());

        CreatedDriverDTO driver = new CreatedDriverDTO();
        driver.setId(1L);
        driver.setEmail(request.getEmail());
        driver.setFirstName(request.getFirstName());
        driver.setLastName(request.getLastName());
        driver.setPhone(request.getPhone());
        driver.setAddress(request.getAddress());
        driver.setVehicle(vehicle);

        return new ResponseEntity<>(driver, HttpStatus.CREATED);
    }

    @GetMapping(value = "/activation/{token}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateActivationToken(
            @PathVariable String token) {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "/activation/{token}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> setInitialPassword(
            @PathVariable String token,
            @RequestBody SetDriverPasswordDTO request) {

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
