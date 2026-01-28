package com.ftn.heisenbugers.gotaxi.models.dtos;

import com.ftn.heisenbugers.gotaxi.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RideHistoryDTO {
    private UUID rideId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String startAddress;
    private String endAddress;
    private boolean canceled;
    private User canceledBy;
    private double price;
    private boolean panicTriggered;

}
