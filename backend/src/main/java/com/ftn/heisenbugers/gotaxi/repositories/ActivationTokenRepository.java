package com.ftn.heisenbugers.gotaxi.repositories;

import com.ftn.heisenbugers.gotaxi.models.security.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Long> {
    Optional<ActivationToken> findByToken(String token);
    void deleteByUserId(java.util.UUID userId);
}
