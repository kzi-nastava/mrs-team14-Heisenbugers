package com.ftn.heisenbugers.gotaxi.services;

import com.ftn.heisenbugers.gotaxi.models.Notification;
import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    public void notifyUser(User user, String message, Ride ride) {

        Notification notification = Notification.builder()
                .message(message)
                .read(false)
                .user(user)
                .ride(ride)
                .build();

        repository.save(notification);

        messagingTemplate.convertAndSendToUser(
                user.getEmail(), // or username depending on your security
                "/queue/notifications",
                notification.getMessage()
        );
    }
}

