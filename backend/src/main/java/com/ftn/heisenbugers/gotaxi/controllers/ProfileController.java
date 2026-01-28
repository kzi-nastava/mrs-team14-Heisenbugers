package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.Driver;
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
import org.springframework.web.multipart.MultipartFile;

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

        User user = AuthContextService.getCurrentUser();
        return ResponseEntity.ok(profileService.getMyProfile(user.getEmail()));
    }

    @GetMapping(value = "/me/driver", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<Integer> getDriverProfile() throws InvalidUserType {

        Driver driver = AuthContextService.getCurrentDriver();

        return new ResponseEntity<>(driver.getActiveHoursLast24h(), HttpStatus.OK);
    }

    @GetMapping(value = "/me/vehicle", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedVehicleDTO> getMyVehicle() throws InvalidUserType {
        return ResponseEntity.ok(profileService.getMyVehicle());
    }

    @PutMapping(value = "/me",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetProfileDTO> updateProfile(
            @RequestPart("data") GetProfileDTO request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws InvalidUserType {

        User user = AuthContextService.getCurrentUser();
        GetProfileDTO updatedProfile = profileService.updateProfile(user.getEmail(), request, image);
        return ResponseEntity.ok(updatedProfile);
    }

    @PutMapping(value = "/me/vehicle",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedVehicleDTO> updateVehicle(
            @RequestBody CreatedVehicleDTO request) throws InvalidUserType {

        return new ResponseEntity<>(profileService.updateMyVehicle(request), HttpStatus.OK);
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
