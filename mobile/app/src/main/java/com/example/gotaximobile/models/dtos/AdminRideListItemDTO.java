package com.example.gotaximobile.models.dtos;

import com.example.gotaximobile.models.enums.RideStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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


    public UUID getRideId() {
        return rideId;
    }

    public RideStatus getStatus() { return status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }

    public String getStartAddress() { return startAddress; }
    public String getDestinationAddress() { return destinationAddress; }

    public boolean isCanceled() { return canceled; }
    public boolean isPanicTriggered() { return panicTriggered; }

    public BigDecimal getPrice() { return price; }
    public String getCanceledBy() { return canceledBy; }

}
