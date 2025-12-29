package com.ftn.heisenbugers.gotaxi.controller;

import com.ftn.heisenbugers.gotaxi.dto.ChangePasswordDTO;
import com.ftn.heisenbugers.gotaxi.dto.GetDriverProfileDTO;
import com.ftn.heisenbugers.gotaxi.dto.GetProfileDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetProfileDto> getMyProfile() {

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

        GetProfileDto profile = new GetProfileDto();
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
    public ResponseEntity<GetProfileDto> updateProfile(
            @RequestBody GetProfileDto request) {

        GetProfileDto profile = new GetProfileDto();
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
