package com.nracademy.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nracademy.backend.entity.auth.BlackListedToken;

public interface BlackListedTokenRepository extends JpaRepository<BlackListedToken, UUID> {
    boolean existsByToken(String token);

    List<BlackListedToken> findAllByUserEmail(String email);

    void deleteAllByExpiresAtBefore(LocalDateTime expiresAtBefore);
}