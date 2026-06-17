package com.nracademy.backend.service.impl;

import com.nracademy.backend.entity.auth.BlackListedToken;
import com.nracademy.backend.entity.user.User;
import com.nracademy.backend.entity.enums.BlacklistReason;
import com.nracademy.backend.repository.BlackListedTokenRepository;
import com.nracademy.backend.service.UserService;
import com.nracademy.backend.util.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import com.nracademy.backend.service.BlackListService;

@Service
@RequiredArgsConstructor
public class BlackListServiceImpl implements BlackListService {
    private final BlackListedTokenRepository tokenRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Transactional
    public void blacklistToken(String token, BlacklistReason reason) {

        if (isTokenBlacklisted(token) || jwtUtil.isTokenExpired(token)) {
            return;
        }

        LocalDateTime expiresAt = jwtUtil.extractExpirationDateFromToken(token)
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        String email = jwtUtil.extractEmailFromToken(token);

        User user = userService.getUserByEmail(email);

        BlackListedToken blackListedToken = BlackListedToken.builder()
                .token(token)
                .user(user)
                .blacklistedAt(LocalDateTime.now())
                .reason(reason)
                .expiresAt(expiresAt)
                .build();
        tokenRepository.save(blackListedToken);
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenRepository.existsByToken(token);
    }

    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.deleteAllByExpiresAtBefore(now);
    }
}