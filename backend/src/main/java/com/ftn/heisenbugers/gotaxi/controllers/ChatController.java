package com.ftn.heisenbugers.gotaxi.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public String handleMessage(String message) {
        return message;
    }
}

