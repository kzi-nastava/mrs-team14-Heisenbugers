package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.dtos.RideTrackingDTO;
import com.ftn.heisenbugers.gotaxi.services.RideService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/rides")
public class RideController {


    private final RideService rideService;

    public RideController(RideService rideService) {

        this.rideService = rideService;
    }

    @GetMapping("")
    public ResponseEntity<List<RideTrackingDTO>> getRideTracking() {


        return ResponseEntity.ok(rideService.getAll());
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
    public ResponseEntity<Object> reportDriver(@PathVariable UUID rideId, @RequestBody Map<String, Object> body) {
        User user = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        boolean ok = rideService.report(rideId, user.getId(),
                (String) body.get("description"));
        if (!ok) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok().build();
        }

    }

    @PostMapping("/{rideId}/finish")
    public ResponseEntity<Object> finishRide(@PathVariable UUID rideId) {
        Object sub = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        if (!(sub instanceof Driver)) {
            return ResponseEntity.badRequest().build();
        }
        Driver driver = (Driver) sub;
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
    public ResponseEntity<Object> rateDriver(@PathVariable UUID rideId, @RequestBody Map<String, Object> body) {
        Object sub = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        if (!(sub instanceof User rater)) {
            return ResponseEntity.badRequest().build();
        }
        int driverScore = Integer.parseInt((String) body.get("driverScore"));
        int vehicleScore = Integer.parseInt((String) body.get("vehicleScore"));
        String comment = (String) body.get("comment");
        rideService.rate(rideId, rater.getId(), driverScore, vehicleScore, comment);
        return ResponseEntity.ok()
                .body(Map.of("message", "Ride successfully rated"));
    }

}
