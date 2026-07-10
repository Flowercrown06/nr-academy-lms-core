package com.nracademy.backend.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtConfig {

    @Value("${jwt.secret:MyVeryLongSecretKeyThatIsAtLeast32CharactersLong123}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    long expiration;

    @Value("${jwt.refresh-expiration:604800000}")
    long refreshExpiration;

    @Bean
    public SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Bean
    public long getExpiration() {
        return expiration;
    }

    @Bean
    public long getRefreshExpiration() {
        return refreshExpiration;
    }
}