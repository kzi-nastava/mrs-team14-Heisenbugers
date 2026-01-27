package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanicEventDTO {
    private UUID id;
    private boolean resolved;

    private UUID rideId;
    private String message;      // из Notification или отдельного поля (см. ниже)
    private LocalDateTime createdAt;

    private Double vehicleLat;
    private Double vehicleLng;
}
