package com.ftn.heisenbugers.gotaxi.models.dtos;

import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class CreateRideDTO {
    @Setter @Getter
    private RouteDTO route;
    @Setter @Getter
    private VehicleType vehicleType;
    @Setter @Getter
    private boolean babyTransport;
    @Setter @Getter
    private boolean petTransport;
    @Setter @Getter
    private List<String> passengersEmails;
}
