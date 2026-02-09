package com.ftn.heisenbugers.gotaxi.models.dtos;

import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileChangeRequestDTO {
    private boolean approved;

    private String firstName;

    private String lastName;

    private String phone;

    private String address;

    private String profileImageUrl;

    private String model;

    private VehicleType type;

    private String licensePlate;

    private int seatCount;

    private boolean babyTransport = false;

    private boolean petTransport = false;
}
