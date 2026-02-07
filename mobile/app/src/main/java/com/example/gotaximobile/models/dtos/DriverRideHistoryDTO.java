package com.example.gotaximobile.models.dtos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DriverRideHistoryDTO {
    public UUID rideId;
    public String startedAt;
    public String endedAt;
    public String startAddress;
    public String endAddress;
    public boolean canceled;
    public double price;
    public boolean panicTriggered;
    public List<TrafficViolationDTO> trafficViolations;
    public List<PassengerInfoDTO> passengers = new ArrayList<>();
    public List<LocationDTO> route;

}
