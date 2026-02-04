package com.example.gotaximobile.models.dtos;

public class ForgotPasswordResponseDTO {
    private String message;
    private String resetToken;

    public String getMessage() {
        return message;
    }

    public String getResetToken() {
        return resetToken;
    }
}
