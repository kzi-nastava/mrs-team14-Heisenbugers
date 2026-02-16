package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockableUserDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String profileImageUrl;
    private boolean blocked;
    private String role;
}
