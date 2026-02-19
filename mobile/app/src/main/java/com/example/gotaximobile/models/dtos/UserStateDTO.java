package com.example.gotaximobile.models.dtos;

import com.example.gotaximobile.models.enums.UserState;

import java.util.UUID;

public class UserStateDTO {
    public UserState state;
    public UUID rideId;

    public UserState getState() {
        return state;
    }

    public UUID getRideId() {
        return rideId;
    }
}
