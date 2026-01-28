package com.ftn.heisenbugers.gotaxi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private String content;

    @NotNull
    private LocalDateTime sentAt;

    @NotNull
    private boolean received;

    private LocalDateTime receivedAt;

    @NotNull
    private boolean seen;

    private LocalDateTime seenAt;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    @NotNull
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User sender;

    @PrePersist
    public void prePersist() {
        sentAt = LocalDateTime.now();
    }
}


