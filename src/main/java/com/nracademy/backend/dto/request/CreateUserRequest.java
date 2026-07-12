package com.nracademy.backend.dto.request;

import com.nracademy.backend.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateUserRequest {
    private UUID courseId;
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    @Email
    private String email;
    @Pattern(
            regexp = "^(\\+994|994|0)(50|51|55|70|77|99)\\d{7}$",
            message = "Invalid Azerbaijani phone number"
    )
    private String phone;
    @NotBlank
    private String password;
    @NotNull
    private Role role;
}
