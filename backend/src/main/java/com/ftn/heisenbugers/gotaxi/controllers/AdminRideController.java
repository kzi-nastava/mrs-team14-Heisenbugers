package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.Location;
import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.Route;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.dtos.*;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.repositories.RatingRepository;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.repositories.TrafficViolationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
    @Autowired
    private TrafficViolationRepository violationRepository;

    @Autowired
    private RatingRepository ratingRepository;

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


        List<Ride> rides = rideRepository.findAll();
        Comparator<Ride> comp = buildRideComparator(sort);
        rides = rides.stream()
                .filter(r -> driverId == null || (r.getDriver() != null && driverId.equals(r.getDriver().getId())))

                .filter(r -> matchesPassenger(r, passengerId))
                .filter(r -> status == null || status == r.getStatus())
                .filter(r -> fromDt == null || (r.getCreatedAt() != null && !r.getCreatedAt().isBefore(fromDt)))
                .filter(r -> toDt == null || (r.getCreatedAt() != null && !r.getCreatedAt().isAfter(toDt)))
                 .sorted(comp)

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

    private boolean hasPassenger(Ride ride, UUID passengerId) {
        if (ride.getPassengers() == null) return false;
        return ride.getPassengers().stream()
                .anyMatch(p -> p != null && passengerId.equals(p.getId()));
    }

    private AdminRideListItemDTO toListItemDTO(Ride ride) {
        //Route route = ride.getRoute();

        String startAddress = (ride.getStart() != null) ? ride.getStart().getAddress() : null;
        String destAddress = (ride.getEnd() != null) ? ride.getEnd().getAddress() : null;

        boolean canceled = ride.isCanceled() || ride.getStatus() == RideStatus.CANCELED;
        boolean panicTriggered = (ride.getPanicEvent() != null);

        String canceledByName = null;
        if (canceled) {
            User cb = ride.getCanceledBy();
            canceledByName = (cb == null) ? "UNKNOWN" : (cb.getFirstName() + " " + cb.getLastName());
        }

        return new AdminRideListItemDTO(
                ride.getId(),
                ride.getStatus(),
                ride.getStartedAt(),
                ride.getEndedAt(),
                startAddress,
                destAddress,
                canceled,
                canceledByName,
                BigDecimal.valueOf(ride.getPrice()),
                panicTriggered
        );
    }

    private AdminRideDetailsDTO toDetailsDTO(Ride ride) {
        Route route = ride.getRoute();

        LocationDTO start = toLocationDTO(route != null ? route.getStart() : null);
        LocationDTO dest = toLocationDTO(route != null ? route.getDestination() : null);

        List<LocationDTO> stops = (route != null && route.getStops() != null)
                ? route.getStops().stream().map(this::toLocationDTO).collect(Collectors.toList())
                : List.of();

        List<LocationDTO> polyline = (route != null)
                ? route.getStops().stream().map(this::toLocationDTO).toList()
                : List.of();

        UUID driverId = (ride.getDriver() != null) ? ride.getDriver().getId() : null;
        String driverName = (ride.getDriver() != null)
                ? ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName()
                : null;

        List<PassengerInfoDTO> passengers = (ride.getPassengers() != null)
                ? ride.getPassengers().stream()
                .filter(p -> p != null)
                .map(p -> new PassengerInfoDTO(
                        p.getId(),
                        p.getFirstName(),
                        p.getLastName(),
                        p.getEmail(),
                        "http://localhost:8081" + p.getProfileImageUrl()
                ))
                .toList()
                : List.of();

        boolean panicTriggered = (ride.getPanicEvent() != null);
        boolean canceled = ride.isCanceled() || ride.getStatus() == RideStatus.CANCELED;
        String canceledByName = null;
        if (canceled) {
            User cb = ride.getCanceledBy();
            canceledByName = (cb == null) ? "UNKNOWN" : (cb.getFirstName() + " " + cb.getLastName());
        }

        List<TrafficViolationDTO> trafficViolations =
                violationRepository.findByRideIdOrderByCreatedAtDesc(ride.getId())
                        .stream()
                        .map(v -> new TrafficViolationDTO(v.getTitle(), v.getDescription()))
                        .toList();

        RatingResponseDTO rating = ratingRepository.findByRideId(ride.getId())
                .map(r -> new RatingResponseDTO(
                        r.getId(),
                        ride.getId(),
                        r.getDriverScore(),
                        r.getVehicleScore(),
                        r.getComment(),
                        r.getCreatedAt()
                ))
                .orElse(null);


        AdminRideDetailsDTO dto = new AdminRideDetailsDTO();
        dto.setRideId(ride.getId());
        dto.setStatus(ride.getStatus());
        dto.setScheduledAt(ride.getScheduledAt());
        dto.setStartedAt(ride.getStartedAt());
        dto.setEndedAt(ride.getEndedAt());
        dto.setPrice(BigDecimal.valueOf(ride.getPrice()));
        dto.setStart(start);
        dto.setDestination(dest);
        dto.setStops(stops);

        dto.setDriverId(driverId);
        dto.setDriverName(driverName);

        dto.setPassengers(passengers);
        dto.setTrafficViolations(trafficViolations);
        dto.setPolyline(polyline);

        dto.setPanicTriggered(panicTriggered);
        dto.setCanceled(canceled);
        dto.setCanceledByName(canceledByName);
        dto.setCancelReason(ride.getCancelReason());
        dto.setCanceledAt(ride.getCanceledAt());

        dto.setRating(rating);

        return dto;
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

    private Comparator<Ride> buildRideComparator(String sort) {
        String s = (sort == null || sort.isBlank()) ? "startedAt,desc" : sort;
        String[] parts = s.split(",");
        String field = parts[0].trim();
        String dir = (parts.length > 1) ? parts[1].trim().toLowerCase() : "desc";

        Comparator<Ride> c;

        switch (field) {
            case "createdAt" -> c = Comparator.comparing(Ride::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case "startedAt" -> c = Comparator.comparing(Ride::getStartedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case "endedAt" -> c = Comparator.comparing(Ride::getEndedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case "price" -> c = Comparator.comparing(Ride::getPrice, Comparator.nullsLast(Comparator.naturalOrder()));
            case "status" -> c = Comparator.comparing(Ride::getStatus, Comparator.nullsLast(Comparator.naturalOrder()));
            case "canceled" -> c = Comparator.comparing(Ride::isCanceled);
            case "panicTriggered" -> c = Comparator.comparing(r -> r.getPanicEvent() != null);
            default -> c = Comparator.comparing(Ride::getStartedAt, Comparator.nullsLast(Comparator.naturalOrder()));
        }

        if ("desc".equals(dir)) c = c.reversed();
        return c;
    }
    private boolean matchesPassenger(Ride r, UUID passengerId) {
        if (passengerId == null) return true;


        if (r.getRoute() != null && r.getRoute().getUser() != null
                && passengerId.equals(r.getRoute().getUser().getId())) {
            return true;
        }


        return hasPassenger(r, passengerId);
    }
}
