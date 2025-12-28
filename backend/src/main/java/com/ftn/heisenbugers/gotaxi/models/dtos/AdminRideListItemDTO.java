package com.ftn.heisenbugers.gotaxi.models.dtos;

import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRideListItemDTO {
    private UUID rideId;
    private RideStatus status;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private String startAddress;
    private String destinationAddress;

    private boolean canceled;
    private String canceledBy;      //  "UNKNOWN"
    private BigDecimal price;

    private boolean panicTriggered;
}
