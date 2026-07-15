package com.nracademy.backend.dto.response;

import com.nracademy.backend.entity.enums.Role;
import com.nracademy.backend.entity.enums.UserStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserMeResponse {
    UUID id;
    UUID courseId;
    String name;
    String surname;
    String email;
    String phone;
    Role role;
    UserStatus status;
    LocalDateTime createdAt;
    LocalDateTime lastLoginAt;
}
