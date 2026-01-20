package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.dtos.DriverRideHistoryDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.RideSort;
import com.ftn.heisenbugers.gotaxi.services.DriverService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping("/{driverId}/history")
    public ResponseEntity<List<DriverRideHistoryDTO>> getDriverRideHistory(
            @PathVariable UUID driverId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "DATE") RideSort sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        List<DriverRideHistoryDTO> history =
                driverService.getDriverHistory(driverId, startDate, endDate, sortBy, direction);
        return ResponseEntity.ok(history);

    }


}
