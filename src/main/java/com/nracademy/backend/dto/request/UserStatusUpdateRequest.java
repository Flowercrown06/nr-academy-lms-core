package com.nracademy.backend.dto.request;

import com.nracademy.backend.entity.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserStatusUpdateRequest {
    @NotNull
    private UserStatus status;
    private String reason;
}
