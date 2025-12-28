package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleInfoDTO {
    private UUID id;
    private String model;
    private String licensePlate;
    private double latitude;
    private double longitude;
    private boolean occupied;
}
