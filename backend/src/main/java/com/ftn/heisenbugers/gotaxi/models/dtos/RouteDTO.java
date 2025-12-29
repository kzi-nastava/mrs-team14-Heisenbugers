package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class RouteDTO {
    @Getter @Setter
    private double distanceKm;
    @Getter @Setter
    private int estimatedTimeMin;
    @Getter @Setter
    private String polyline;
    @Getter @Setter
    private LocationDTO start;
    @Getter @Setter
    private LocationDTO destination;
    @Getter @Setter
    private List<LocationDTO> stops;
}
