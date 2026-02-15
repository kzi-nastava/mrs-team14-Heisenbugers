package com.ftn.heisenbugers.gotaxi.services;

import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Location;
import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.dtos.StopRideRequestDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.repositories.LocationRepository;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RideActionsService {

    private final RideRepository rideRepository;
    private final LocationRepository locationRepository;

    public ResponseEntity<?> stopRide(UUID rideId, StopRideRequestDTO request) throws InvalidUserType {

        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Ride not found."));
        }

        if (ride.getStatus() != RideStatus.ONGOING) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ride is not ongoing."));
        }

        Driver currentDriver = AuthContextService.getCurrentDriver();
        if (ride.getDriver() == null || !ride.getDriver().getId().equals(currentDriver.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only ride driver can stop the ride."));
        }

        if (request == null || request.getLatitude() == null || request.getLongitude() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Stop location (latitude/longitude) is required."));
        }

        if (ride.getStart() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ride has no start location."));
        }

        Location oldEnd = ride.getEnd();

        String addr = (request.getAddress() != null && !request.getAddress().trim().isEmpty())
                ? request.getAddress().trim()
                : ("Stopped at: " + request.getLatitude() + ", " + request.getLongitude());

        Location stopLocation = Location.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(addr)
                .build();

        stopLocation = locationRepository.save(stopLocation);

        ride.setEnd(stopLocation);
        ride.setEndedAt(LocalDateTime.now());
        ride.setStatus(RideStatus.FINISHED);
        rideRepository.save(ride);

        double newPrice = recalcPriceOnStop(ride, oldEnd, stopLocation);
        ride.setPrice(newPrice);
        rideRepository.save(ride);

        return ResponseEntity.ok(Map.of(
                "message", "Ride stopped and finished.",
                "rideId", ride.getId().toString(),
                "endedAt", ride.getEndedAt().toString(),
                "newDestination", Map.of(
                        "latitude", stopLocation.getLatitude(),
                        "longitude", stopLocation.getLongitude(),
                        "address", stopLocation.getAddress()
                ),
                "price", ride.getPrice()
        ));
    }

    double recalcPriceOnStop(Ride ride, Location oldEnd, Location stop) {
        double oldPrice = ride.getPrice();
        if (oldPrice <= 0) return oldPrice;

        Location start = ride.getStart();
        if (start == null || oldEnd == null) return oldPrice;

        double fullKm = haversineKm(start.getLatitude(), start.getLongitude(),
                oldEnd.getLatitude(), oldEnd.getLongitude());

        double stopKm = haversineKm(start.getLatitude(), start.getLongitude(),
                stop.getLatitude(), stop.getLongitude());

        if (fullKm <= 0.1) return oldPrice;

        double ratio = stopKm / fullKm;
        ratio = Math.max(0.10, Math.min(ratio, 1.00));

        double newPrice = oldPrice * ratio;
        return Math.round(newPrice * 100.0) / 100.0;
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
