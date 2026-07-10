package com.nracademy.backend.dto.response;

import com.nracademy.backend.entity.enums.Role;
import com.nracademy.backend.entity.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class UserDto {
    UUID id;
    UUID courseId;
    String name;
    String surname;
    String email;
    String phone;
    Role role;
    UserStatus status;
    Instant createdAt;
}
