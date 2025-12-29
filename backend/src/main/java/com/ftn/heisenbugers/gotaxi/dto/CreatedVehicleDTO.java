package com.ftn.heisenbugers.gotaxi.dto;

import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class CreatedVehicleDTO {
    @Getter @Setter
    private Long id;
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
