package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class MessageDTO {
    private UUID chatId;
    private String content;
    private String from;
    private LocalDateTime sentAt;
}
