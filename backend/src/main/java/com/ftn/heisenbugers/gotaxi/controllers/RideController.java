package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Location;
import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.Route;
import com.ftn.heisenbugers.gotaxi.models.dtos.LocationDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.RideTrackingDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.repositories.DriverRepository;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RideRepository rideRepository;

    @GetMapping("")
    public ResponseEntity<List<RideTrackingDTO>> getRideTracking() {
        List<Ride> rides = rideRepository.findAll();

        List<RideTrackingDTO> trackingDTOs = rides.stream()
                .map(ride -> {
                    RideTrackingDTO dto = new RideTrackingDTO();
                    dto.setRideId(ride.getId());

                    Driver driver = ride.getDriver();
                    dto.setVehicleLatitude(driver.getLocation().getLatitude());
                    dto.setVehicleLongitude(driver.getLocation().getLongitude());
                    dto.setVehicleAddress(driver.getLocation().getAddress());
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(trackingDTOs);
    }


    @GetMapping("/{rideId}/tracking")
    public ResponseEntity<RideTrackingDTO> getRideTracking(@PathVariable UUID rideId) {
        Optional<Ride> rideOpt = rideRepository.findById(rideId);
        Ride ride = rideOpt.orElse(null);

        if (ride == null || ride.getStatus() != RideStatus.ONGOING) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Get current vehicle location
        Driver driver = ride.getDriver();
        Location currentLocation = driver.getLocation();

        // Calculate remaining time based on route and current position
        Route route = ride.getRoute();
        List<LocationDTO> routeDTOs = new ArrayList<>();
        if (route != null) {
            // Convert route locations to DTOs
            routeDTOs = route.getStops().stream()
                    .map(location -> new LocationDTO(location.getLatitude(), location.getLongitude(), location.getAddress()))
                    .toList();
        }

        RideTrackingDTO trackingDTO = new RideTrackingDTO(
                rideId,
                currentLocation.getLatitude(),
                currentLocation.getLongitude(),
                currentLocation.getAddress(),
                0,
                routeDTOs
        );

        return ResponseEntity.ok(trackingDTO);
    }


}
