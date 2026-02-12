package com.ftn.heisenbugers.gotaxi.repositories;

import com.ftn.heisenbugers.gotaxi.models.Chat;
import com.ftn.heisenbugers.gotaxi.models.User;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
    Chat findByRequesterEmail(String requesterEmail);

    @Override
    @NullMarked
    Optional<Chat> findById(UUID id);

    List<Chat> getByRequester(User requester);

    Optional<Chat> findByRequester(User requester);
}
