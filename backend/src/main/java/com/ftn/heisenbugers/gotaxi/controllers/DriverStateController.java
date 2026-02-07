package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.dtos.DriverWorkingDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.MessageResponse;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.repositories.DriverRepository;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/driver")
public class DriverStateController {

    private final DriverRepository driverRepository;
    private final RideRepository rideRepository;

    public DriverStateController(DriverRepository driverRepository,
                                 RideRepository rideRepository) {
        this.driverRepository = driverRepository;
        this.rideRepository = rideRepository;
    }

    @GetMapping("/me/working")
    public ResponseEntity<DriverWorkingDTO> getMyWorkingState() throws InvalidUserType {
        Driver driver = AuthContextService.getCurrentDriver();
        return ResponseEntity.ok(new DriverWorkingDTO(driver.isWorking()));
    }
    @PutMapping("/me/working")
    public ResponseEntity<?> setMyWorkingState(@RequestBody DriverWorkingDTO request)
            throws InvalidUserType {

        if (request == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Request body is required."));
        }

        Driver driver = AuthContextService.getCurrentDriver();
        boolean targetWorking = request.isWorking();

        if (driver.isWorking() == targetWorking) {
            return ResponseEntity.ok(new DriverWorkingDTO(driver.isWorking()));
        }

        if (!targetWorking) {
            List<Ride> active = rideRepository.findActiveRidesByDriver(driver.getId());
            boolean hasActive =
                    active.stream().anyMatch(r ->
                            r.getStatus() == RideStatus.ASSIGNED ||
                                    r.getStatus() == RideStatus.ONGOING);

            if (hasActive) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new MessageResponse(
                                "You cannot go inactive while you have an assigned or ongoing ride."
                        ));
            }
        }

        driver.setWorking(targetWorking);
        driverRepository.save(driver);

        return ResponseEntity.ok(new DriverWorkingDTO(driver.isWorking()));
    }
}

