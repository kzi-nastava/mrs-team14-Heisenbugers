package com.ftn.heisenbugers.gotaxi.models.dtos;

import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    private boolean panicTriggered;
}
