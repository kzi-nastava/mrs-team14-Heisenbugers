package com.example.gotaximobile.models.dtos;

import java.util.UUID;

public class RegisterResponseDTO {
    private UUID userId;
    private String message;
    public RegisterResponseDTO(UUID userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
