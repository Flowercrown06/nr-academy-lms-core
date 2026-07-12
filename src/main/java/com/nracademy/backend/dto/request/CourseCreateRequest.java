package com.nracademy.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseCreateRequest {
    @NotBlank
    private String name;
    private String legalName;
    @Email
    private String contactEmail;
    @Pattern(
            regexp = "^(\\+994|994|0)(50|51|55|70|77|99)\\d{7}$",
            message = "Invalid Azerbaijani phone number"
    )
    private String contactPhone;
    @NotBlank
    private String ownerName;
    @NotBlank
    private String ownerSurname;
    @NotBlank
    @Email
    private String ownerEmail;
    @NotBlank
    private String ownerPassword;
    @NotNull
    private BigDecimal commissionRate;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal paymentPerStudent;
}
