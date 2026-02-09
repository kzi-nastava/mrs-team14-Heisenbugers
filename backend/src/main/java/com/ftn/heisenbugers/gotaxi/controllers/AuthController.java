package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.Administrator;
import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Passenger;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.dtos.*;
import com.ftn.heisenbugers.gotaxi.models.security.JwtService;
import com.ftn.heisenbugers.gotaxi.models.services.AuthService;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.models.dtos.MessageResponse;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private AuthService authService;
    private UserRepository userRepository;
    private final RideRepository rideRepository;

    @Autowired
    private JwtService jwtService;



    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO resp = authService.login(request);
        return ResponseEntity.ok(resp);
    }


    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@ModelAttribute RegisterPassengerRequestDTO request) {

        String appBaseUrl = "http://localhost:4200";

        var userId = authService.registerPassenger(request, appBaseUrl);

        RegisterResponseDTO resp = new RegisterResponseDTO(
                userId,
                "Registration successful. Check your email to activate your account."
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    //activation from token
    @GetMapping("/activate")
    public ResponseEntity<?> activate(@RequestParam("token") String token) {
        authService.activateByToken(token);
        //return ResponseEntity.ok(new MessageResponse("Account activated. You can log in."));
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("http://localhost:4200/auth/login?activated=1"));

        return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302
    }

    //admin manual activation
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

        String appBaseUrl = "http://localhost:4200";

        authService.requestPasswordReset(request.getEmail(), appBaseUrl);

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
        if (request.getToken() == null || request.getToken().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Reset token is required."));
        }

        authService.resetPassword(request.getToken(), request.getNewPassword());


        return ResponseEntity.ok(new MessageResponse("Password updated."));
    }

    //logout
    @DeleteMapping("/session")
    public ResponseEntity<MessageResponse> logout() throws InvalidUserType {

        User current = AuthContextService.getCurrentUser();

        if (current instanceof Driver driver) {

            boolean hasActiveRide = !rideRepository.findActiveRidesByDriver(driver.getId()).isEmpty();

            if (hasActiveRide) {

                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new MessageResponse("You cannot log out while you have an active ride."));
            }


            driver.setWorking(false);
            userRepository.save(driver);
        }
        return ResponseEntity.ok(new MessageResponse("Logged out."));
    }

}
