package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.dtos.VehicleInfoDTO;
import com.ftn.heisenbugers.gotaxi.services.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicInfoController {

    private final VehicleService vehicleService;

    public PublicInfoController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleInfoDTO>> getAllActiveVehicles() {
        return ResponseEntity.ok(vehicleService.getActiveVehicleDtos());
    }
}
