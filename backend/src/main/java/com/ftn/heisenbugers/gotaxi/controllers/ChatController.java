package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.Chat;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.dtos.MessageDTO;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.repositories.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Controller
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate template;
    private final ChatRepository chatRepository;

    @MessageMapping("/sendMessage")
    public void handleMessage(MessageDTO message, Principal principal) throws AccessDeniedException {
        String currentUserEmail = principal.getName();
        Chat currentUserChat = chatRepository.findByRequesterEmail(currentUserEmail);
        Chat messageChat = chatRepository.findById(message.getChatId()).get();
        String messageUserEmail = messageChat.getRequester().getEmail();

        if (!messageChat.getId().equals(currentUserChat.getId()) && !isAdmin(principal)) {
            throw new AccessDeniedException("Cannot send to this chat");
        }

        message.setSentAt(LocalDateTime.now());

        // Send to user
        template.convertAndSendToUser(messageUserEmail, "/queue/messages", message);

        // Send to admins
        template.convertAndSend("/topic/admin", message);
    }

    @GetMapping("/api/me/chat")
    public UUID getChat() throws InvalidUserType {
        User user = AuthContextService.getCurrentUser();
        Chat chat = chatRepository.findByRequester(user).orElseGet(() -> {
            Chat newChat = new Chat();
            newChat.setRequester(user);
            chatRepository.save(newChat);
            return newChat;
        });
        return chat.getId();
    }

    private boolean isAdmin(Principal principal) {
        if (principal instanceof Authentication auth) {
            return auth.getAuthorities().stream()
                    .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));
        }
        return false;
    }
}

