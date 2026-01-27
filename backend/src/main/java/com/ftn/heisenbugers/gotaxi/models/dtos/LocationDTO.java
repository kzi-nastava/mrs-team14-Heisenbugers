package com.ftn.heisenbugers.gotaxi.models.dtos;

import com.ftn.heisenbugers.gotaxi.models.Location;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    @Getter
    @Setter
    private double latitude;
    @Getter
    @Setter
    private double longitude;
    @Getter
    @Setter
    private String address;

    public LocationDTO(Location l) {
        this.latitude = l.getLatitude();
        this.longitude = l.getLongitude();
        this.address = l.getAddress();
    }
}
