package com.ftn.heisenbugers.gotaxi.models.dtos;

import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

public class CreatedRideDTO {
    @Setter @Getter
    private UUID Id;
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
    @Setter @Getter
    private DriverDto driver;
    @Setter @Getter
    private RideStatus status;
}
