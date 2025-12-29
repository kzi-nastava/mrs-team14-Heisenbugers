package com.ftn.heisenbugers.gotaxi.models.dtos;

import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class CreateVehicleDTO {
    @Getter @Setter
    private String vehicleModel;
    @Getter @Setter
    private VehicleType vehicleType;
    @Getter @Setter
    private String licensePlate;
    @Getter @Setter
    private int seatCount;
    @Getter @Setter
    private boolean babyTransport;
    @Getter @Setter
    private boolean petTransport;
}
