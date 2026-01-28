package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverProfileRequestListDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
}