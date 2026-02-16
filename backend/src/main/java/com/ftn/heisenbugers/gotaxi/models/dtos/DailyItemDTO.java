package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyItemDTO {
    private String date;
    private long rides;
    private double kilometers;
    private double money;
}