package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.Chat;
import com.ftn.heisenbugers.gotaxi.models.Message;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.dtos.ChatDescDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.DriverDto;
import com.ftn.heisenbugers.gotaxi.models.dtos.MessageDTO;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.repositories.ChatRepository;
import com.ftn.heisenbugers.gotaxi.repositories.MessageRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Controller
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate template;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    private final SimpUserRegistry userRegistry;

    @MessageMapping("/sendMessage")
    public void handleMessage(MessageDTO message, Principal principal) throws AccessDeniedException {
        String currentUserEmail = principal.getName();
        Chat currentUserChat = chatRepository.findByRequesterEmail(currentUserEmail);
        Chat messageChat = chatRepository.findById(message.getChatId()).get();
        String messageUserEmail = messageChat.getRequester().getEmail();

        if (!isAdmin(principal) && !messageChat.getId().equals(currentUserChat.getId())) {
            throw new AccessDeniedException("Cannot send to this chat");
        }

        message.setSentAt(LocalDateTime.now());

        User currentUser = userRepository.findByEmail(currentUserEmail).get();
        Message m = new Message();
        m.setContent(message.getContent());
        m.setChat(messageChat);
        m.setSender(currentUser);
        m.setActive(true);
        m.setCreatedBy(currentUser);
        m.setLastModifiedBy(currentUser);
        messageRepository.save(m);

        message.setFrom(currentUserEmail);

        userRegistry.getUsers().forEach(user -> {
            user.getSessions().forEach(session -> {
                session.getSubscriptions().forEach(sub -> {
                    System.out.println("User: " + user.getName() + ", SessionId: " + session.getId() + ", Subscribed to: " + sub.getDestination());
                });
            });
        });
        // Send to user
        template.convertAndSendToUser(messageUserEmail, "/queue/messages", message);

        // Send to admins
        template.convertAndSend("/topic/admin/chat/" + messageChat.getId(), message);
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

    @GetMapping("/api/me/chat/{chatId}")
    public UUID getChatById(@PathVariable String chatId) throws InvalidUserType {
        UUID chatUUID = UUID.fromString(chatId);
        Chat chat = chatRepository.getChatById(chatUUID).get();
        return chat.getId();
    }

    @GetMapping("api/me/chat/full")
    public List<MessageDTO> getFullChat() throws InvalidUserType {
        User user = AuthContextService.getCurrentUser();
        Optional<Chat> chatOpt = chatRepository.findByRequester(user);
        if (chatOpt.isPresent()) {
            Chat chat = chatOpt.get();
            return messageRepository.getAllByChat(chat).stream().map(msg -> new MessageDTO(
                    msg.getChat().getId(), msg.getContent(),
                    msg.getSender().getEmail(), msg.getSentAt()
            )).toList();
        } else {
            return List.of();
        }
    }

    @GetMapping("api/me/chat/{chatId}/full")
    public List<MessageDTO> getFullChatById(@PathVariable String chatId) throws InvalidUserType {
        UUID chatUUID = UUID.fromString(chatId);
        Chat chat = chatRepository.getChatById(chatUUID).get();
        return messageRepository.getAllByChat(chat).stream().map(msg -> new MessageDTO(
                msg.getChat().getId(), msg.getContent(),
                msg.getSender().getEmail(), msg.getSentAt()
        )).toList();
    }

    @GetMapping("api/admin/chats")
    public List<ChatDescDTO> getAllChats(Principal principal) throws AccessDeniedException {
        if (!isAdmin(principal)) {
            throw new AccessDeniedException("Only admins can access this endpoint");
        }
        List<Chat> chats = chatRepository.findAll();
        return chats.stream().map(c -> new ChatDescDTO(
                c.getId(),
                new DriverDto(c.getRequester().getFirstName(), c.getRequester().getLastName())
        )).toList();
    }

    private boolean isAdmin(Principal principal) {
        if (principal instanceof Authentication auth) {
            return auth.getAuthorities().stream()
                    .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));
        }
        return false;
    }
}

