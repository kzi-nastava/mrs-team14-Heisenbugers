package com.ftn.heisenbugers.gotaxi.models.dtos;

import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
public class StartedRideDTO {
    @Getter @Setter
    private Long rideId;
    @Getter @Setter
    private RideStatus status;
    @Getter @Setter
    private LocalDateTime startedAt;
}
