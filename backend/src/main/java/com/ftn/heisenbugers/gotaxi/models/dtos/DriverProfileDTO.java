package com.ftn.heisenbugers.gotaxi.models.dtos;

import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriverProfileDTO {
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String profileImageUrl;
    private String model;
    private VehicleType type;
    private String licensePlate;
    private int seatCount;
    private boolean babyTransport;
    private boolean petTransport;
}