package com.nracademy.backend.entity.auth;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

import com.nracademy.backend.entity.user.User;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "refresh_token",
    indexes = {
        @Index(name="idx_refresh_user", columnList="user_id"),
        @Index(name="idx_refresh_exp", columnList="expiry_date")
    }
)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(unique = true, nullable = false)
    String token;

    @Column(name = "expiry_date", nullable = false)
    LocalDateTime expiryDate;

    @Builder.Default
    @Column(nullable = false)
    boolean revoked = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;
    
    @Column(name = "revoked_at")
    LocalDateTime revokedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void updateRevokedAt() {
        this.revokedAt = LocalDateTime.now();
    }
}