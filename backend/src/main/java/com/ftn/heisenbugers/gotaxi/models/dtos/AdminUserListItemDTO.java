package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AdminUserListItemDTO {
    private UUID id;
    private String fullName;
    private String email;
    private String profileImageUrl;
}
