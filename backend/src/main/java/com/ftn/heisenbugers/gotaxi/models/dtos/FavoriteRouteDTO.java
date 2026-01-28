package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteRouteDTO {
    private UUID Id;
    private LocationDTO startAddress;
    private LocationDTO endAddress;

    private List<LocationDTO> stops;

    private double distanceKm;
    private int timeMin;
}
