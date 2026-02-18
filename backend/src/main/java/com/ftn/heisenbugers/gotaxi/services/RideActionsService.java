package com.ftn.heisenbugers.gotaxi.services;

import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.*;
import com.ftn.heisenbugers.gotaxi.models.dtos.StopRideRequestDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.models.services.EmailService;
import com.ftn.heisenbugers.gotaxi.repositories.LocationRepository;
import com.ftn.heisenbugers.gotaxi.repositories.PassengerRepository;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RideActionsService {

    private final RideRepository rideRepository;
    private final LocationRepository locationRepository;
    private final EmailService emailService;
    private final PassengerRepository passengerRepository;
    private final UserRepository userRepository;

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

        currentDriver.setAvailable(true);
        userRepository.save(currentDriver);

        ride.setEnd(stopLocation);
        ride.setEndedAt(LocalDateTime.now());
        ride.setStatus(RideStatus.FINISHED);
        rideRepository.save(ride);

        double newPrice = recalcPriceOnStop(ride, oldEnd, stopLocation);
        ride.setPrice(newPrice);

        rideRepository.save(ride);


        for (User u : ride.getPassengers()) {
            sendFinishedRideEmail(u, ride);
            if (Objects.equals(u.getFirstName(), "")) {
                passengerRepository.delete((Passenger) u);
            }
        }

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

    private void sendFinishedRideEmail(User recipient, Ride ride) {
        String subject = "Subject: Your Ride Has Completed – Share Your Feedback!";
        String body =
                """
                        Dear %s %s,
                        
                        Your ride from %s to %s with us has successfully concluded. We hope you had a smooth and enjoyable journey!
                        
                        We would love to hear about your experience. You can leave a review by visiting your ride history in your profile.
                        
                        Thank you for choosing our service. We look forward to serving you again soon!
                        
                        Best regards, \s
                        GoTaxi 
                        """.formatted(!Objects.equals(recipient.getFirstName(), "") ? recipient.getFirstName() : recipient.getEmail(), recipient.getLastName(),
                        ride.getStart().getAddress(), ride.getEnd().getAddress());
        emailService.sendMail(recipient.getEmail(), subject, body);
    }
}
