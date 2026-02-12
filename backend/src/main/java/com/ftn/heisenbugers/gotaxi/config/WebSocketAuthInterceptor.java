package com.ftn.heisenbugers.gotaxi.config;

import com.ftn.heisenbugers.gotaxi.models.Administrator;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.security.JwtService;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public WebSocketAuthInterceptor(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        System.out.println("Headers: " + accessor.toNativeHeaderMap());

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            try {
                String authHeader = accessor.getFirstNativeHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    String email = jwtService.extractEmail(token);
                    User user = userRepository.findByEmail(email).orElse(null);
                    SimpleGrantedAuthority authority;
                    if (user instanceof Administrator) {
                        authority = new SimpleGrantedAuthority("ROLE_ADMIN");
                    } else {
                        authority = new SimpleGrantedAuthority("ROLE_USER");
                    }
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            user.getEmail(), null, List.of(authority)
                    );

                    accessor.setUser(authentication);

                    System.out.println("✅ WebSocket user authenticated: " + user.getEmail());

                } else {
                    System.err.println("❌ No Authorization header found in WebSocket CONNECT");
                }
            } catch (Exception e) {
                System.err.println("❌ JWT validation failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return message;
    }
}