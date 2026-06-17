package com.nracademy.backend.service;

import com.nracademy.backend.entity.enums.BlacklistReason;

public interface BlackListService {
    void blacklistToken(String token, BlacklistReason reason);
    boolean isTokenBlacklisted(String token);
    void cleanupExpiredTokens();
}
