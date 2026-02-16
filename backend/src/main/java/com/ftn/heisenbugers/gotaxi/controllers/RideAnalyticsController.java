package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.dtos.RideAnalyticsResponseDTO;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.services.RideAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin
public class RideAnalyticsController {

    private final RideAnalyticsService analyticsService;

    @GetMapping("/rides")
    public ResponseEntity<RideAnalyticsResponseDTO> getRideAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "false") boolean aggregate
    ) throws InvalidUserType {

        if (!Objects.equals(role, "ADMIN") && !aggregate){
            User user = AuthContextService.getCurrentUser();
            return ResponseEntity.ok(analyticsService.getAnalytics(
                    start,
                    end,
                    role,
                    user.getId(),
                    false
            ));
        }

        return ResponseEntity.ok(analyticsService.getAnalytics(
                start,
                end,
                role,
                userId != null ? UUID.fromString(userId) : null,
                aggregate
        ));
    }
}