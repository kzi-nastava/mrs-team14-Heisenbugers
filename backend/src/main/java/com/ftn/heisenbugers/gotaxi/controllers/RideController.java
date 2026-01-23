package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.dtos.RideTrackingDTO;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.services.RideService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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

}
