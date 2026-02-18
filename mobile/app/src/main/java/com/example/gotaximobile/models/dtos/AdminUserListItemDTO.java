package com.example.gotaximobile.models.dtos;

import java.util.UUID;

public class AdminUserListItemDTO {
    private UUID id;
    private String fullName;
    private String email;
    private String profileImageUrl;

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public UUID getId() {
        return id;
    }
}
