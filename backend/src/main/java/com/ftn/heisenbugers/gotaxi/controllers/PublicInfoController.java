package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Vehicle;
import com.ftn.heisenbugers.gotaxi.models.dtos.VehicleInfoDTO;
import com.ftn.heisenbugers.gotaxi.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
public class PublicInfoController {

    @Autowired
    private VehicleRepository vehicleRepository;

    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleInfoDTO>> getAllActiveVehicles() {
        List<Vehicle> activeVehicles = vehicleRepository.findAll();

        List<VehicleInfoDTO> vehicleDTOs = activeVehicles.stream()
                .map(vehicle -> {
                    VehicleInfoDTO dto = new VehicleInfoDTO();
                    dto.setId(vehicle.getId());
                    dto.setModel(vehicle.getModel());
                    dto.setLicensePlate(vehicle.getLicensePlate());

                    // Get current location from driver's last known position
                    Driver driver = vehicle.getDriver();
                    if (driver != null && driver.getLocation() != null) {
                        dto.setLatitude(driver.getLocation().getLatitude());
                        dto.setLongitude(driver.getLocation().getLongitude());

                        // Check if vehicle is occupied (has active ride)
                        dto.setOccupied(driver.isAvailable());
                    }


                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(vehicleDTOs);
    }
}
