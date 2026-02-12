package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate template;

    @SendTo("/sendMessage")
    public void handleMessage(Message message, Principal principal) throws AccessDeniedException {
        String currentUserEmail = principal.getName();
        String chatUserEmail = message.getChat().getRequester().getEmail();

        if (!chatUserEmail.equals(currentUserEmail) && !isAdmin(principal)) {
            throw new AccessDeniedException("Cannot send to this chat");
        }

        message.setSentAt(LocalDateTime.now());

        // Send to user
        template.convertAndSendToUser(chatUserEmail, "/queue/messages", message);

        // Send to admins
        template.convertAndSend("/topic/admin", message);
    }

    private boolean isAdmin(Principal principal) {
        if (principal instanceof Authentication auth) {
            return auth.getAuthorities().stream()
                    .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));
        }
        return false;
    }
}

