package com.example.gotaximobile.models.dtos;

import com.example.gotaximobile.models.enums.RideStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AdminRideDetailsDTO {
    private UUID rideId;
    private RideStatus status;

    private LocalDateTime scheduledAt;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private BigDecimal price;

    private LocationDTO start;
    private LocationDTO destination;
    private List<LocationDTO> stops;

    private UUID driverId;
    private String driverName;

    private UUID passengerId;
    private String passengerName;
    private List<PassengerInfoDTO> passengers;

    private List<TrafficViolationDTO> trafficViolations;
    private RatingResponseDTO rating;
    private List<LocationDTO> polyline;

    private boolean panicTriggered;
    private boolean canceled;
    private String canceledByName;
    private String cancelReason;
    private LocalDateTime canceledAt;


    public UUID getRideId() {
        return rideId;
    }

    public RideStatus getStatus() {
        return status;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public LocationDTO getStart() {
        return start;
    }

    public LocationDTO getDestination() {
        return destination;
    }

    public List<LocationDTO> getStops() {
        return stops;
    }

    public UUID getDriverId() {
        return driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public UUID getPassengerId() {
        return passengerId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public List<PassengerInfoDTO> getPassengers() {
        return passengers;
    }

    public List<TrafficViolationDTO> getTrafficViolations() {
        return trafficViolations;
    }

    public RatingResponseDTO getRating() {
        return rating;
    }

    public List<LocationDTO> getPolyline() {
        return polyline;
    }

    public boolean isPanicTriggered() {
        return panicTriggered;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public String getCanceledByName() {
        return canceledByName;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public LocalDateTime getCanceledAt() {
        return canceledAt;
    }
}
