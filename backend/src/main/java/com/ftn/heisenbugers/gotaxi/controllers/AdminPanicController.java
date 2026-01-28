package com.ftn.heisenbugers.gotaxi.controllers;


import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.*;
import com.ftn.heisenbugers.gotaxi.models.dtos.MessageResponse;
import com.ftn.heisenbugers.gotaxi.models.dtos.PanicEventDTO;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.repositories.NotificationRepository;
import com.ftn.heisenbugers.gotaxi.repositories.PanicEventRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminPanicController {
    private final PanicEventRepository panicRepository;
    private final NotificationRepository notificationRepository;

    public AdminPanicController(PanicEventRepository panicRepository,
                                NotificationRepository notificationRepository) {
        this.panicRepository = panicRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/panic/active")
    public ResponseEntity<?> getActivePanics() throws InvalidUserType {
        // admin?
        AuthContextService.getCurrentAdmin();

        //return ResponseEntity.ok(panicRepository.findByResolvedFalseOrderByCreatedAtDesc());
        var events = panicRepository.findByResolvedFalseOrderByCreatedAtDesc();
        var dtos = events.stream()
                .map(pe -> new PanicEventDTO(
                        pe.getId(),
                        pe.isResolved(),
                        pe.getRide() != null ? pe.getRide().getId() : null,
                        pe.getMessage(),
                        pe.getCreatedAt(),
                        pe.getVehicleLat(),
                        pe.getVehicleLng()
                ))
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/panic/{panicId}/resolve")
    public ResponseEntity<?> resolve(@PathVariable UUID panicId) throws InvalidUserType {
        Administrator admin = AuthContextService.getCurrentAdmin();

        PanicEvent pe = panicRepository.findById(panicId).orElse(null);
        if (pe == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Panic not found."));
        }

        pe.setResolved(true);
        pe.setHandledBy(admin);
        pe.setLastModifiedBy(admin);
        panicRepository.save(pe);

        return ResponseEntity.ok(new MessageResponse("Panic resolved."));
    }

    @GetMapping("/notifications/unread")
    public ResponseEntity<?> unreadNotifs() throws InvalidUserType {
        Administrator admin = AuthContextService.getCurrentAdmin();

        return ResponseEntity.ok(
                notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(admin.getId())
        );
    }

    @PostMapping("/notifications/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable UUID id) throws InvalidUserType {
        Administrator admin = AuthContextService.getCurrentAdmin();

        Notification n = notificationRepository.findById(id).orElse(null);
        if (n == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        if (n.getUser() == null || !n.getUser().getId().equals(admin.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        n.setRead(true);
        n.setReadAt(LocalDateTime.now());
        n.setLastModifiedBy(admin);
        notificationRepository.save(n);

        return ResponseEntity.ok().build();
    }

}
