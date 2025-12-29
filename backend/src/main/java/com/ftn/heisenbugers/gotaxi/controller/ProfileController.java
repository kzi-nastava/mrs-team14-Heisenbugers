package com.ftn.heisenbugers.gotaxi.controller;

import com.ftn.heisenbugers.gotaxi.models.dtos.ChangePasswordDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.GetDriverProfileDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.GetProfileDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetProfileDTO> getMyProfile() {

        if (/*loggedUser instanceof Driver*/false) {

            GetDriverProfileDTO driverProfile = new GetDriverProfileDTO();
            driverProfile.setId(1L);
            driverProfile.setEmail("driver@test.com");
            driverProfile.setFirstName("Marko");
            driverProfile.setLastName("Marković");
            driverProfile.setAvailable(true);
            driverProfile.setActiveHoursLast24h(5);

            return new ResponseEntity<>(driverProfile, HttpStatus.OK);
        }

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

    @PutMapping(value = "/me/password",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordDTO request) {

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
