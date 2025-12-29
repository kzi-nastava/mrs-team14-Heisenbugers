package com.ftn.heisenbugers.gotaxi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class GetDriverProfileDTO extends GetProfileDto {
    @Getter @Setter
    private boolean available;
    @Getter @Setter
    private int activeHoursLast24h;
    @Getter @Setter
    private CreatedVehicleDTO vehicle;
}
