package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.Administrator;
import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Passenger;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.dtos.*;
import com.ftn.heisenbugers.gotaxi.models.services.AuthService;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@CrossOrigin(
        origins = "http://localhost:4200",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private AuthService authService;
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

    //delete?
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

        if (user instanceof Driver) return "DRIVER";
        if (user instanceof Passenger) return "PASSENGER";
        if (user instanceof Administrator) return "ADMIN";
        return "USER";
    }


}
