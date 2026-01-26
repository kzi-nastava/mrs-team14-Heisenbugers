package com.ftn.heisenbugers.gotaxi.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftn.heisenbugers.gotaxi.models.dtos.LocationDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.RideEstimateRequestDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.RideEstimateResponseDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "http://localhost:4200")
public class PublicRideEstimateController {

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/ride-estimates")
    public ResponseEntity<RideEstimateResponseDTO> estimate(@RequestBody RideEstimateRequestDTO request) {

        //double distanceKm = 0;
        //long durationSec = 0;

        try{
        // estimatedPrice = BigDecimal.valueOf(distanceKm * 120);
        String url = "https://router.project-osrm.org/route/v1/driving/"
                + request.getStart().getLongitude() + "," + request.getStart().getLatitude()
                + ";" + request.getDestination().getLongitude() + "," + request.getDestination().getLatitude()
                + "?overview=full&geometries=geojson";

        String raw = rest.getForObject(url, String.class);
        JsonNode root = mapper.readTree(raw);

        JsonNode routes = root.path("routes");
        if (!routes.isArray() || routes.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }
        JsonNode best = routes.get(0);

        double distanceMeters = best.path("distance").asDouble(); // meters
        double durationSeconds = best.path("duration").asDouble(); // seconds

        double distanceKm = distanceMeters / 1000.0;

        int estimatedTimeMin = (int) Math.ceil(durationSeconds / 60.0);

        BigDecimal kmPart = BigDecimal.valueOf(distanceKm).multiply(BigDecimal.valueOf(120));
        BigDecimal base = basePrice(request.getVehicleType());
        BigDecimal estimatedPrice = base.add(kmPart).setScale(0, RoundingMode.HALF_UP);


        List<LocationDTO> routePoints = new ArrayList<>();
        JsonNode coords = best.path("geometry").path("coordinates");
        if (coords.isArray()) {
            for (JsonNode c : coords) {
                double lon = c.get(0).asDouble();
                double lat = c.get(1).asDouble();
                routePoints.add(new LocationDTO(lat, lon, "")); 
            }
        }


        RideEstimateResponseDTO resp = new RideEstimateResponseDTO(
                distanceKm,
                estimatedTimeMin,
                estimatedPrice,
                "",
                routePoints
        );

        return ResponseEntity.ok(resp);
        }catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
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