package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DriverRideHistoryDTO {
    private UUID rideId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String startAddress;
    private String endAddress;
    private boolean canceled;
    private String canceledBy;
    private double price;
    private boolean panicTriggered;
    private List<PassengerInfoDTO> passengers;
}
