package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.dtos.DriverRideHistoryDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.RideSort;
import com.ftn.heisenbugers.gotaxi.services.DriverService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping("/history")
    public ResponseEntity<List<DriverRideHistoryDTO>> getDriverRideHistory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "DATE") RideSort sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Object sub = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        if (!(sub instanceof Driver driver)) {
            return ResponseEntity.badRequest().build();
        }

        List<DriverRideHistoryDTO> history =
                driverService.getDriverHistory(driver.getId(), startDate, endDate, sortBy, direction);
        return ResponseEntity.ok(history);

    }


}
