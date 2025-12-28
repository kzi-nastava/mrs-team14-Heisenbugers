package com.ftn.heisenbugers.gotaxi.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RideSort {
    DATE("startedAt"),
    PRICE("price"),
    DESTINATION("end.address");

    private final String property;

}
