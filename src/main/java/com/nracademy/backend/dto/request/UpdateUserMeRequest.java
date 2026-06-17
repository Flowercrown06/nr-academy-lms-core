package com.nracademy.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserMeRequest {
    @NotBlank
    String name;
    @NotBlank
    String surname;
    @NotBlank
    String email;
    @NotBlank
    String phone;
}