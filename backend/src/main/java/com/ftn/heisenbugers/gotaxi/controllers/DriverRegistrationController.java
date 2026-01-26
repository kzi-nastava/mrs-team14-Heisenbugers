package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.dtos.CreateDriverDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.CreatedDriverDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.CreatedVehicleDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.SetDriverPasswordDTO;
import com.ftn.heisenbugers.gotaxi.models.services.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/drivers")
public class DriverRegistrationController {

    private final AuthService authService;

    public DriverRegistrationController (AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CreatedDriverDTO> createDriver(
            @RequestBody CreateDriverDTO request) {

        var userId = authService.registerDriver(request);

        CreatedVehicleDTO vehicle = new CreatedVehicleDTO();
        vehicle.setId(UUID.randomUUID());
        vehicle.setModel(request.getVehicle().getVehicleModel());
        vehicle.setType(request.getVehicle().getVehicleType());
        vehicle.setLicensePlate(request.getVehicle().getLicensePlate());
        vehicle.setSeatCount(request.getVehicle().getSeatCount());
        vehicle.setBabyTransport(request.getVehicle().isBabyTransport());
        vehicle.setPetTransport(request.getVehicle().isPetTransport());

        CreatedDriverDTO driver = new CreatedDriverDTO();
        driver.setId(userId);
        driver.setEmail(request.getEmail());
        driver.setFirstName(request.getFirstName());
        driver.setLastName(request.getLastName());
        driver.setPhone(request.getPhone());
        driver.setAddress(request.getAddress());
        driver.setVehicle(vehicle);

        return new ResponseEntity<>(driver, HttpStatus.CREATED);
    }

    @GetMapping(value = "/activate",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateActivationToken(
            @RequestParam("token") String token) {

        try{
            authService.activateByToken(token);
        } catch (Exception e) {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("http://localhost:4200/auth/token-used"));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("http://localhost:4200/auth/set-password?token="+token));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @PutMapping(value = "/password",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> setInitialPassword(
            @RequestParam("token") String token,
            @RequestBody SetDriverPasswordDTO request) {

        authService.setInitialPasswordForDriver(request, token);

        return ResponseEntity.ok().build();
    }
}
