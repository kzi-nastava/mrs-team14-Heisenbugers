package com.ftn.heisenbugers.gotaxi.models.dtos;

import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class CreatedVehicleDTO {
    @Getter @Setter
    private UUID id;
    @Getter @Setter
    private String model;
    @Getter @Setter
    private VehicleType type;
    @Getter @Setter
    private String licensePlate;
    @Getter @Setter
    private int seatCount;
    @Getter @Setter
    private boolean babyTransport;
    @Getter @Setter
    private boolean petTransport;
}
