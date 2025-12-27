package com.ftn.heisenbugers.gotaxi.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message extends BaseEntity {

    private String content;

    private LocalDateTime sentAt;

    private boolean received;

    private LocalDateTime receivedAt;

    private boolean seen;

    private LocalDateTime seenAt;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User sender;

    @PrePersist
    public void prePersist() {
        sentAt = LocalDateTime.now();
    }
}


