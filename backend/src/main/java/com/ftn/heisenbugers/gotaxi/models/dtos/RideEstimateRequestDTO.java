package com.ftn.heisenbugers.gotaxi.models.dtos;

import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class RideEstimateRequestDTO {
    @Getter @Setter
    private LocationDTO start;
    @Getter @Setter
    private LocationDTO destination;
    @Getter @Setter
    private List<LocationDTO> stops;   // optional
    @Getter @Setter
    private VehicleType vehicleType;   // STANDARD/LUXURY/VAN
    @Getter @Setter
    private boolean babyTransport;
    @Getter @Setter
    private boolean petTransport;
}
