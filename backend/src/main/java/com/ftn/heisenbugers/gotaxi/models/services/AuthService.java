package com.ftn.heisenbugers.gotaxi.models.services;

import com.ftn.heisenbugers.gotaxi.models.*;
import com.ftn.heisenbugers.gotaxi.models.dtos.*;
import com.ftn.heisenbugers.gotaxi.models.security.ActivationToken;
import com.ftn.heisenbugers.gotaxi.models.security.JwtService;
import com.ftn.heisenbugers.gotaxi.repositories.ActivationTokenRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import com.ftn.heisenbugers.gotaxi.repositories.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final VehicleRepository vehicleRepository;


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
        p.setPasswordHash(passwordEncoder.encode(request.getPassword()));
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

    public UUID registerDriver(CreateDriverDTO request) {

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required.");
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists.");
        }

        Vehicle v = new Vehicle();
        v.setModel(request.getVehicle().getVehicleModel());
        v.setType(request.getVehicle().getVehicleType());
        v.setLicensePlate(request.getVehicle().getLicensePlate());
        v.setSeatCount(request.getVehicle().getSeatCount());
        v.setBabyTransport(request.getVehicle().isBabyTransport());
        v.setPetTransport(request.getVehicle().isPetTransport());

        Driver d = new Driver();
        d.setEmail(normalizedEmail);
        d.setFirstName(request.getFirstName());
        d.setLastName(request.getLastName());
        d.setPasswordHash(passwordEncoder.encode(request.getEmail()+request.getLastName()));
        d.setPhone(request.getPhone());
        d.setAddress(request.getAddress());
        d.setProfileImageUrl(request.getProfileImageUrl());
        d.setBlocked(false);
        d.setActivated(false);
        d.setVehicle(v);
        v.setDriver(d);

        userRepository.save(d);

        String token = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");

        ActivationToken activationToken = ActivationToken.builder()
                .token(token)
                .user(d)
                .expiresAt(Instant.now().plus(Duration.ofHours(24)))
                .used(false)
                .build();

        activationTokenRepository.save(activationToken);


        String activationLink = "http://localhost:8081/api/drivers/activate?token=" + token;
        emailService.sendActivationEmail(normalizedEmail, activationLink);

        return d.getId();
    }

    public void setInitialPasswordForDriver(SetDriverPasswordDTO request, String token){
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }

        ActivationToken at = activationTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid activation token."));

        User user = userRepository.findByEmail(at.getUser().getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
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


    public LoginResponseDTO login(LoginRequestDTO dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank()
                || dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email and password are required.");
        }

        String normalizedEmail = dto.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials."));

        if (!user.isActivated()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is not activated.");
        }


        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
        }

        String role = resolveRole(user);

        Map<String, Object> claims = Map.of(
                "uid", user.getId().toString(),
                "role", role
        );

        String token = jwtService.generateToken(user.getEmail(), claims);

        return new LoginResponseDTO(
                token,
                "Bearer",
                user.getId(),
                role
        );
    }

    private String resolveRole(User user) {
        if (user instanceof Driver) {
            return "DRIVER";
        } else if (user instanceof Administrator) {
            return "ADMIN";
        } else {
            return "PASSENGER";
        }
    }
}
