package com.ftn.heisenbugers.gotaxi.models.dtos;


import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDTO(
        UUID id,
        String message,
        boolean read,
        LocalDateTime createdAt,
        LocalDateTime readAt,
        UUID rideId,
        String redirectUrl
) {

}
