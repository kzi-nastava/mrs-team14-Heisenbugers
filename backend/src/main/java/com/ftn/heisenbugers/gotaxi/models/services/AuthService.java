package com.ftn.heisenbugers.gotaxi.models.services;

import com.ftn.heisenbugers.gotaxi.models.Passenger;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.dtos.RegisterPassengerRequestDTO;
import com.ftn.heisenbugers.gotaxi.models.security.ActivationToken;
import com.ftn.heisenbugers.gotaxi.repositories.ActivationTokenRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final EmailService emailService;


    public UUID registerPassenger(RegisterPassengerRequestDTO request, String appBaseUrl) {

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required.");
        }
        if (request.getPassword() == null || request.getConfirmPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password and confirmPassword are required.");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match.");
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();


        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists.");
        }


        Passenger p = new Passenger();
        p.setEmail(normalizedEmail);
        p.setPasswordHash(request.getPassword()); // PasswordEncoder позже обсудим
        p.setFirstName(request.getFirstName());
        p.setLastName(request.getLastName());
        p.setPhone(request.getPhone());
        p.setAddress(request.getAddress());
        p.setProfileImageUrl(request.getProfileImageUrl());
        p.setBlocked(false);
        p.setActivated(false);

        userRepository.save(p);


        String token = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");

        ActivationToken activationToken = ActivationToken.builder()
                .token(token)
                .user(p)
                .expiresAt(Instant.now().plus(Duration.ofHours(24)))
                .used(false)
                .build();

        activationTokenRepository.save(activationToken);


       // String link = appBaseUrl + "/auth/activate?token=" + token;
       // emailService.sendActivationEmail(p.getEmail(), link);
        String activationLink = "http://localhost:8081/api/auth/activate?token=" + token;
        emailService.sendActivationEmail(normalizedEmail, activationLink);

        return p.getId();
    }


    public void activateByToken(String token) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is required.");
        }

        ActivationToken at = activationTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid activation token."));

        if (at.isUsed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token already used.");
        }
        if (Instant.now().isAfter(at.getExpiresAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expired.");
        }

        User user = at.getUser();
        if (user.isActivated()) {
            at.setUsed(true);
            activationTokenRepository.save(at);
            return;
        }

        user.setActivated(true);
        userRepository.save(user);

        at.setUsed(true);
        activationTokenRepository.save(at);
    }


}
