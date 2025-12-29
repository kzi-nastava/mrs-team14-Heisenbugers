package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDTO {
    private UUID userId;
    private String message;

    // for test without email
   // private String activationToken;
}
