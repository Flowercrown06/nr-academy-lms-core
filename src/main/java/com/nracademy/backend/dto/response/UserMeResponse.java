package com.nracademy.backend.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.nracademy.backend.entity.enums.RoleType;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserMeResponse {
    UUID id;
    String name;
    String surname;
    String email;
    String phone;
    boolean isActive;
    String profileImageUrl;
    Set<RoleType> roles;
    LocalDateTime createdAt;
    LocalDateTime lastLoginAt;
}