package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.Passenger;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.dtos.ChangePasswordDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.CreatedVehicleDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.GetDriverProfileDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.GetProfileDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.services.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetProfileDTO> getMyProfile() throws InvalidUserType {

        Passenger passenger = AuthContextService.getCurrentPassenger();
        return ResponseEntity.ok(profileService.getMyProfile(passenger.getEmail()));
    }

    @GetMapping(value = "/me/driver", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<GetDriverProfileDTO> getDriverProfile() {

        GetDriverProfileDTO driverProfile = new GetDriverProfileDTO();
        driverProfile.setId(UUID.randomUUID());
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
        vehicle.setId(UUID.randomUUID());
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
            @RequestBody GetProfileDTO request) throws InvalidUserType {

        User user = AuthContextService.getCurrentUser();
        GetProfileDTO updatedProfile = profileService.updateProfile(user.getEmail(), request);
        return ResponseEntity.ok(updatedProfile);
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
            @RequestBody ChangePasswordDTO request) throws InvalidUserType {

        User user = AuthContextService.getCurrentUser();
        profileService.changePassword(user.getEmail(), request);
        return ResponseEntity.ok().build();
    }
}
