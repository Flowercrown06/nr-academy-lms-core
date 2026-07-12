package com.nracademy.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(
            regexp = "^(\\+994|994|0)(50|51|55|70|77|99)\\d{7}$",
            message = "Invalid Azerbaijani phone number"
    )
    String phone;
}