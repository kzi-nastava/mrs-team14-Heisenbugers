package com.ftn.heisenbugers.gotaxi.services;

import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Vehicle;
import com.ftn.heisenbugers.gotaxi.models.dtos.VehicleInfoDTO;
import com.ftn.heisenbugers.gotaxi.repositories.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<VehicleInfoDTO> getActiveVehicleDtos() {
        List<Vehicle> activeVehicles = vehicleRepository.getVehiclesByDriverWorking(true);

        return activeVehicles.stream()
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
                .toList();
    }
}
