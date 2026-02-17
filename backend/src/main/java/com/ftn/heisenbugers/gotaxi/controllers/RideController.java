package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.dtos.*;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.models.security.JwtService;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.services.RideService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideRepository rideRepository;
    private final RideService rideService;
    private final JwtService jwtService;

    @GetMapping("")
    public ResponseEntity<List<RideTrackingDTO>> getRideTracking() {


        return ResponseEntity.ok(rideService.getAll());
    }

    @GetMapping("/{rideId}")
    public ResponseEntity<RideDTO> getRide(@PathVariable UUID rideId) {
        RideDTO ride = rideService.getRide(rideId);
        if (ride == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(ride);
        }
    }

    @GetMapping("/me/active")
    public ResponseEntity<?> getMyActiveRide() throws InvalidUserType {
        Driver driver = AuthContextService.getCurrentDriver();

        var rideOpt = rideRepository.findByDriverIdAndStatus(
                driver.getId(),
                RideStatus.ASSIGNED
        );

        if (rideOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("No active ride."));
        }

        Ride ride = rideOpt.get();

        AssignedRideDTO dto = new AssignedRideDTO();
        dto.setRideId(ride.getId());

        if (ride.getRoute() != null) {
            if (ride.getRoute().getStart() != null) {
                dto.setStart(new LocationDTO(ride.getRoute().getStart().getLatitude(), ride.getRoute().getStart().getLongitude(),
                        ride.getRoute().getStart().getAddress()
                ));
            }

            if (ride.getRoute().getDestination() != null) {
                dto.setEnd(new LocationDTO(ride.getRoute().getDestination().getLatitude(), ride.getRoute().getDestination().getLongitude(),
                        ride.getRoute().getDestination().getAddress()
                ));
            }

            if (ride.getRoute().getStopsWithAddresses() != null) {
                List<LocationDTO> stops = ride.getRoute().getStopsWithAddresses().stream()
                        .map(l -> new LocationDTO(
                                l.getLatitude(),
                                l.getLongitude(),
                                l.getAddress()
                        ))
                        .toList();
                dto.setStops(stops);
            }

            dto.setDistanceKm(ride.getRoute().getDistanceKm());
            dto.setEstimatedTimeMin(ride.getRoute().getEstimatedTimeMin());
        }

        if (ride.getPassengers() != null) {
            List<PassengerInfoDTO> passengers = ride.getPassengers().stream()
                    .map(p -> new PassengerInfoDTO(
                            p.getId(),
                            p.getFirstName(),
                            p.getLastName(),
                            p.getEmail(),
                            "http://localhost:8081" + p.getProfileImageUrl()
                    ))
                    .toList();
            dto.setPassengers(passengers);
        }

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<Void> start(@PathVariable UUID id) {
        rideService.start(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{rideId}/tracking")
    public ResponseEntity<RideTrackingDTO> getRideTracking(@PathVariable UUID rideId) {
        RideTrackingDTO ride = rideService.getRideTrackingById(rideId);
        if (ride == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(ride);
        }
    }

    @PostMapping("/{rideId}/report")
    public ResponseEntity<Object> reportDriver(@PathVariable UUID rideId, @RequestBody Map<String, Object> body) throws InvalidUserType {
        User user = AuthContextService.getCurrentUser();
        boolean ok = rideService.report(rideId, user.getId(),
                (String) body.get("title"),
                (String) body.get("desc"));
        if (!ok) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok().build();
        }

    }

    @PostMapping("/{rideId}/finish")
    public ResponseEntity<Object> finishRide(@PathVariable UUID rideId) throws InvalidUserType {
        Driver driver = AuthContextService.getCurrentDriver();
        boolean ok = rideService.finish(rideId, driver.getId());
        if (!ok) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok().body(
                    Map.of("message", "Ride finished successfully")
            );
        }

    }

    @PostMapping("/{rideId}/rate")
    public ResponseEntity<Object> rateDriver(@PathVariable UUID rideId, @RequestBody Map<String, Object> body) throws InvalidUserType {
        User rater = AuthContextService.getCurrentUser();
        System.out.println(body);
        System.out.println(body.get("driverScore").getClass().getName());
        int driverScore = (Integer) body.get("driverScore");
        int vehicleScore = (Integer) body.get("vehicleScore");
        String comment = (String) body.get("comment");
        boolean ok = rideService.rate(rideId, rater.getId(), driverScore, vehicleScore, comment);
        if (ok) {
            return ResponseEntity.ok()
                    .body(Map.of("message", "Ride successfully rated"));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/link-tracking/tracking")
    public ResponseEntity<RideTrackingDTO> getLinkTracking(@RequestParam String token) {
        Claims claims = jwtService.parseClaims(token);
        UUID rideId = UUID.fromString(claims.get("rideId", String.class));

        RideTrackingDTO ride = rideService.getRideTrackingById(rideId);
        if (ride == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(ride);
        }
    }

    @GetMapping("/link-tracking/ride")
    public ResponseEntity<RideDTO> getLinkTrackingRide(@RequestParam String token) {
        Claims claims = jwtService.parseClaims(token);
        UUID rideId = UUID.fromString(claims.get("rideId", String.class));
        
        RideDTO ride = rideService.getRide(rideId);
        if (ride == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(ride);
        }
    }

}
