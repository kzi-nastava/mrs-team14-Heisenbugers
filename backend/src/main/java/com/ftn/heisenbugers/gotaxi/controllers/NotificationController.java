package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.Notification;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.dtos.NotificationDTO;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository repository;

    @GetMapping("/unread")
    public List<NotificationDTO> getUnread() throws InvalidUserType {

        User user = AuthContextService.getCurrentUser(); // fetch user by principal.getName()

        return repository.findByUserIdAndReadFalse(user.getId())
                .stream()
                .map(Notification::toDto)
                .toList();
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable UUID id) {

        Notification notification = repository.findById(id).orElseThrow();

        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());

        repository.save(notification);
    }
}
