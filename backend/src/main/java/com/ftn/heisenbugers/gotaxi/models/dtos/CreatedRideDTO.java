package com.ftn.heisenbugers.gotaxi.models.dtos;

import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class CreatedRideDTO {
    @Setter @Getter
    private Long Id;
    @Setter @Getter
    private RouteDTO route;
    @Setter @Getter
    private VehicleType vehicleType;
    @Setter @Getter
    private boolean babyTransport;
    @Setter @Getter
    private boolean petTransport;
    @Setter @Getter
    private List<PassengerInfoDTO> passengers;
    @Setter @Getter
    private DriverDto driver;
    @Setter @Getter
    private RideStatus status;
}
