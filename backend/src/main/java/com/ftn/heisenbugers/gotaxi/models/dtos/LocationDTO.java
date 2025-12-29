package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    @Getter @Setter
    private double latitude;
    @Getter @Setter
    private double longitude;
    @Getter @Setter
    private String address;
}
