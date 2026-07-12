package com.nracademy.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CourseUpdateRequest {
    private String name;
    private String legalName;
    @Email
    private String contactEmail;
    @Pattern(
            regexp = "^(\\+994|994|0)(50|51|55|70|77|99)\\d{7}$",
            message = "Invalid Azerbaijani phone number"
    )
    private String contactPhone;
}
