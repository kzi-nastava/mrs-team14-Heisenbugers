package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.dtos.MessageResponse;
import com.ftn.heisenbugers.gotaxi.models.dtos.RideTrackingDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.services.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    @Autowired
    private RideRepository rideRepository;
    private final RideService rideService;

    public RideController(RideService rideService) {

        this.rideService = rideService;
    }



    @GetMapping("")
    public ResponseEntity<List<RideTrackingDTO>> getRideTracking() {


        return ResponseEntity.ok(rideService.getAll());
    }

    @GetMapping("/me/active")
    public ResponseEntity<?> getMyActiveRide() throws InvalidUserType {
        Driver driver = AuthContextService.getCurrentDriver();

        var rideOpt = rideRepository.findFirstByDriverIdAndStatusInOrderByScheduledAtAsc(
                driver.getId(),
                List.of(RideStatus.ASSIGNED, RideStatus.ONGOING)
        );

        if (rideOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("No active ride."));
        }


        Ride ride = rideOpt.get();
        return ResponseEntity.ok(Map.of(
                "rideId", ride.getId(),
                "status", ride.getStatus(),
                "start", ride.getStart(),
                "end", ride.getEnd(),
                "passengers", ride.getPassengers(),
                "scheduledAt", ride.getScheduledAt()
        ));
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
                (String) body.get("description"));
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
        int driverScore = Integer.parseInt((String) body.get("driverScore"));
        int vehicleScore = Integer.parseInt((String) body.get("vehicleScore"));
        String comment = (String) body.get("comment");
        rideService.rate(rideId, rater.getId(), driverScore, vehicleScore, comment);
        return ResponseEntity.ok()
                .body(Map.of("message", "Ride successfully rated"));
    }

}
