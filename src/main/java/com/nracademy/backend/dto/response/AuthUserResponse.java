package com.nracademy.backend.dto.response;

import com.nracademy.backend.entity.enums.Role;
import com.nracademy.backend.entity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserResponse {
    UUID id;
    UUID courseId;
    String email;
    Role role;
    UserStatus status;
}
