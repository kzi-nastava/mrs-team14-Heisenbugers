package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.dtos.LocationDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.RideEstimateRequestDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.RideEstimateResponseDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicRideEstimateController {

    @PostMapping("/ride-estimates")
    public ResponseEntity<RideEstimateResponseDTO> estimate(@RequestBody RideEstimateRequestDTO request) {

        double distanceKm = 0;
        long durationSec = 0;


        BigDecimal estimatedPrice = BigDecimal.valueOf(distanceKm * 120);

        List<LocationDTO> points = new ArrayList<>();
        if (request.getStart() != null) points.add(request.getStart());
        if (request.getStops() != null) points.addAll(request.getStops());
        if (request.getDestination() != null) points.add(request.getDestination());

        RideEstimateResponseDTO resp = new RideEstimateResponseDTO(
                distanceKm,
                (int) durationSec,
                estimatedPrice,
                "",
                points
        );

        return ResponseEntity.ok(resp);
    }

    private BigDecimal basePrice(VehicleType type) {
        if (type == null) return BigDecimal.ZERO;
        return switch (type) {
            case STANDARD -> BigDecimal.ZERO;
            case LUXURY -> BigDecimal.valueOf(300);
            case VAN -> BigDecimal.valueOf(200);
        };
    }
}