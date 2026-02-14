package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class ChatDescDTO {
    public UUID chatId;
    public DriverDto driver;
}
