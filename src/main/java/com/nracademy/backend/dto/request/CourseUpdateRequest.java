package com.nracademy.backend.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class CourseUpdateRequest {
    private String name;
    private String legalName;
    @Email
    private String contactEmail;
    private String contactPhone;
}
