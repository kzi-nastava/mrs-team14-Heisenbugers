/*package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.Location;
import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.Route;
import com.ftn.heisenbugers.gotaxi.models.dtos.*;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/rides")
public class AdminRideController {

    @Autowired
    private RideRepository rideRepository;

    // list + filters
    @GetMapping("")
    public ResponseEntity<?> search(
            @RequestParam(required = false) UUID driverId,
            @RequestParam(required = false) UUID passengerId,
            @RequestParam(required = false) RideStatus status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false, defaultValue = "startedAt,desc") String sort
    ) {
        LocalDateTime fromDt = parseDateTime(from);
        LocalDateTime toDt = parseDateTime(to);

        if ((from != null && fromDt == null) || (to != null && toDt == null)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Invalid date format. Use ISO-8601, e.g. 2025-12-28T10:15:30"));
        }

        //
        List<Ride> rides = rideRepository.findAll();

        rides = rides.stream()
                .filter(r -> driverId == null || (r.getDriver() != null && driverId.equals(r.getDriver().getId())))
                .filter(r -> passengerId == null || (r.getPassenger() != null && passengerId.equals(r.getPassenger().getId())))
                .filter(r -> status == null || status == r.getStatus())
                .filter(r -> fromDt == null || (r.getStartedAt() != null && !r.getStartedAt().isBefore(fromDt)))
                .filter(r -> toDt == null || (r.getStartedAt() != null && !r.getStartedAt().isAfter(toDt)))
                .sorted(Comparator.comparing(Ride::getStartedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .collect(Collectors.toList());

        List<AdminRideListItemDTO> dto = rides.stream()
                .map(this::toListItemDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dto);
    }

    // details
    @GetMapping("/{rideId}")
    public ResponseEntity<?> getRideDetails(@PathVariable UUID rideId) {
        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Ride not found."));
        }
        return ResponseEntity.ok(toDetailsDTO(ride));
    }

    private AdminRideListItemDTO toListItemDTO(Ride ride) {
        Route route = ride.getRoute();

        String startAddress = route != null && route.getStart() != null ? route.getStart().getAddress() : null;
        String destAddress = route != null && route.getDestination() != null ? route.getDestination().getAddress() : null;

        boolean canceled = ride.getStatus() == RideStatus.CANCELED;
        boolean panicTriggered = (ride.getPanicEvent() != null);

        return new AdminRideListItemDTO(
                ride.getId(),
                ride.getStatus(),
                ride.getStartedAt(),
                ride.getEndedAt(),
                startAddress,
                destAddress,
                canceled,
                canceledBy,
                ride.getPrice(),
                panicTriggered
        );
    }

    private AdminRideDetailsDTO toDetailsDTO(Ride ride) {
        Route route = ride.getRoute();

        LocationDTO start = toLocationDTO(route != null ? route.getStart() : null);
        LocationDTO dest = toLocationDTO(route != null ? route.getDestination() : null);

        List<LocationDTO> stops = route != null && route.getStops() != null
                ? route.getStops().stream().map(this::toLocationDTO).toList()
                : List.of();

        UUID driverId = ride.getDriver() != null ? ride.getDriver().getId() : null;
        String driverName = ride.getDriver() != null ? ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName() : null;

        UUID passengerId = ride.getPassenger() != null ? ride.getPassenger().getId() : null;
        String passengerName = ride.getPassenger() != null ? ride.getPassenger().getFirstName() + " " + ride.getPassenger().getLastName() : null;

        boolean panicTriggered = (ride.getPanicEvent() != null);

        return new AdminRideDetailsDTO(
                ride.getId(),
                ride.getStatus(),
                ride.getScheduledAt(),
                ride.getStartedAt(),
                ride.getEndedAt(),
                ride.getPrice(),
                start,
                dest,
                stops,
                driverId,
                driverName,
                passengerId,
                passengerName,
                panicTriggered
        );
    }

    private LocationDTO toLocationDTO(Location loc) {
        if (loc == null) return null;
        return new LocationDTO(loc.getLatitude(), loc.getLongitude(), loc.getAddress());
    }

    private LocalDateTime parseDateTime(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDateTime.parse(s);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
*/