package com.ftn.heisenbugers.gotaxi.services;

import com.ftn.heisenbugers.gotaxi.models.Notification;
import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    private final SimpUserRegistry simpUserRegistry;

    public void notifyUser(User user, String message) {
        notifyUser(user, message, null, null);
    }

    public void notifyUser(User user, String message, Ride ride) {
        notifyUser(user, message, ride, null);
    }

    public void notifyUser(User user, String message, String redirectUrl) {
        notifyUser(user, message, null, redirectUrl);
    }

    public void notifyUser(User user, String message, Ride ride, String redirectUrl) {

        Notification notification = Notification.builder()
                .message(message)
                .read(false)
                .user(user)
                .ride(ride)
                .redirectUrl(redirectUrl)
                .build();

        repository.save(notification);

        System.out.println("📡 Sending WebSocket notification:");
        System.out.println("   User ID: " + user.getEmail());
        System.out.println("   Destination: " + "/queue/notifications");
        System.out.println("   Full path will be: /user/" + user.getEmail() + "/queue/notifications");
        System.out.println("   Response: " + Notification.toDto(notification));

        System.out.println("All active socket sessions are: ");
        logActiveWebSocketSessions();

        messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/queue/notifications",
                Notification.toDto(notification)
        );
    }

    public void logActiveWebSocketSessions() {
        simpUserRegistry.getUsers().forEach(user -> {
            System.out.println("User: " + user.getName());
            user.getSessions().forEach(session -> {
                System.out.println("  Session ID: " + session.getId());
                session.getSubscriptions().forEach(sub -> {
                    System.out.println("    Subscription ID: " + sub.getId());
                    System.out.println("    Destination: " + sub.getDestination());
                });
            });
        });
    }
}

