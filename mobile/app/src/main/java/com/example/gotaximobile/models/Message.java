package com.example.gotaximobile.models;

import java.time.LocalDateTime;

public class Message {
    public String content;
    public String from;
    public LocalDateTime sentAt;

    public Message(String content, String from, LocalDateTime sentAt) {
        this.content = content;
        this.from = from;
        this.sentAt = sentAt;
    }
}

