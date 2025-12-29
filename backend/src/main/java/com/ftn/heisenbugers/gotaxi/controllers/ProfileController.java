package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.dtos.ChangePasswordDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.CreatedVehicleDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.GetDriverProfileDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.GetProfileDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetProfileDTO> getMyProfile() {

        GetProfileDTO profile = new GetProfileDTO();
        profile.setId(1L);
        profile.setEmail("user@test.com");
        profile.setFirstName("Petar");
        profile.setLastName("Petrović");
        profile.setPhoneNumber("061123456");
        profile.setAddress("Bulevar, Novi Sad");
        profile.setProfileImageUrl("url");

        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @GetMapping(value = "/me/driver", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<GetDriverProfileDTO> getDriverProfile() {

        GetDriverProfileDTO driverProfile = new GetDriverProfileDTO();
        driverProfile.setId(1L);
        driverProfile.setEmail("driver@test.com");
        driverProfile.setFirstName("Marko");
        driverProfile.setLastName("Marković");
        driverProfile.setAvailable(true);
        driverProfile.setProfileImageUrl("url");
        driverProfile.setActiveHoursLast24h(5);

        return new ResponseEntity<>(driverProfile, HttpStatus.OK);
    }

    @GetMapping(value = "/me/vehicle", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedVehicleDTO> getMyVehicle() {
        CreatedVehicleDTO vehicle = new CreatedVehicleDTO();
        vehicle.setId(1L);
        vehicle.setModel("Ford Fiesta");
        vehicle.setType(VehicleType.STANDARD);
        vehicle.setLicensePlate("NS-253-KL");
        vehicle.setSeatCount(5);
        vehicle.setBabyTransport(true);
        vehicle.setPetTransport(false);

        return new ResponseEntity<>(vehicle, HttpStatus.OK);
    }

    @PutMapping(value = "/me",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetProfileDTO> updateProfile(
            @RequestBody GetProfileDTO request) {

        GetProfileDTO profile = new GetProfileDTO();
        profile.setId(1L);
        profile.setEmail(request.getEmail());
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setAddress(request.getAddress());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setProfileImageUrl(request.getProfileImageUrl());

        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @PutMapping(value = "/me/vehicle",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedVehicleDTO> updateVehicle(
            @RequestBody CreatedVehicleDTO request) {

        CreatedVehicleDTO vehicle = new CreatedVehicleDTO();
        vehicle.setId(request.getId());
        vehicle.setModel(request.getModel());
        vehicle.setType(request.getType());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setSeatCount(request.getSeatCount());
        vehicle.setBabyTransport(request.isBabyTransport());
        vehicle.setPetTransport(request.isPetTransport());

        return new ResponseEntity<>(vehicle, HttpStatus.OK);
    }

    @PutMapping(value = "/me/password",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordDTO request) {

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
