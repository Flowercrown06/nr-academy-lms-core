package com.nracademy.backend.util;

import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

    private static final String TOKEN_TYPE = "token_type";
    private static final String ACCESS = "access";
    private static final String REFRESH = "refresh";

    private final SecretKey signingKey;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtUtil(
            SecretKey getSigningKey,
            @Qualifier("getExpiration") long accessExpiration,
            @Qualifier("getRefreshExpiration") long refreshExpiration
    ) {
        this.signingKey = getSigningKey;
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    public String generateAccessToken(String email) {
        return buildToken(email, accessExpiration, ACCESS);
    }

    public String generateRefreshToken(String email) {
        return buildToken(email, refreshExpiration, REFRESH);
    }

    private String buildToken(String subject, long expiration, String type) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(subject)
                .claim(TOKEN_TYPE, type)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaimsFromToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public Date extractExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return extractExpirationDateFromToken(token).before(new Date());
    }

    public boolean isValidAccessToken(Claims claims, String expectedEmail) {
        return expectedEmail != null
                && expectedEmail.equals(claims.getSubject())
                && ACCESS.equals(claims.get(TOKEN_TYPE, String.class))
                && claims.getExpiration() != null
                && claims.getExpiration().after(new Date());
    }

    public boolean isValidRefreshToken(Claims claims) {
        return REFRESH.equals(claims.get(TOKEN_TYPE, String.class))
                && claims.getExpiration() != null
                && claims.getExpiration().after(new Date());
    }
}