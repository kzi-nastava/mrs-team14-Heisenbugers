package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.dtos.*;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Invalid credentials."));
        }


        if (!user.isActivated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Account is not activated."));
        }

        if (user.isBlocked()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("User is blocked."));
        }

        // without hesh
        if (!user.getPasswordHash().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Invalid credentials."));
        }

        LoginResponseDTO resp = new LoginResponseDTO(
                "dummy.jwt.token",
                "Bearer",
                user.getId(),
                resolveRole(user)
        );

        return ResponseEntity.ok(resp);
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterPassengerRequestDTO request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Email is required."));
        }
        if (request.getPassword() == null || request.getConfirmPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Password and confirmPassword are required."));
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Passwords do not match."));
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("Email already exists."));
        }

        Passenger p = new Passenger();
        p.setEmail(request.getEmail());
        p.setPasswordHash(request.getPassword());
        p.setFirstName(request.getFirstName());
        p.setLastName(request.getLastName());
        p.setPhone(request.getPhone());
        p.setAddress(request.getAddress());
        p.setProfileImageUrl(request.getProfileImageUrl());

        p.setBlocked(false);
        p.setActivated(false);

        passengerRepository.save(p);

        RegisterResponseDTO resp = new RegisterResponseDTO(
                p.getId(),
                "Registration successful. Activate your account before login."
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PutMapping("/activate/{userId}")
    public ResponseEntity<?> activate(@PathVariable UUID userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("User not found."));
        }

        if (user.isActivated()) {
            return ResponseEntity.ok(new MessageResponse("Account is already activated."));
        }

        user.setActivated(true);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Account activated."));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new MessageResponse("If the email exists, a reset link has been sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        if (request.getNewPassword() == null || request.getConfirmPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("newPassword and confirmPassword are required."));
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Passwords do not match."));
        }


        // for future token->user
        return ResponseEntity.ok(new MessageResponse("Password updated (stub)."));
    }

    //logout
    @DeleteMapping("/session")
    public ResponseEntity<MessageResponse> logout() {
        return ResponseEntity.ok(new MessageResponse("Logged out."));
    }

    private String resolveRole(User user) {
        return "user";
    }


}
