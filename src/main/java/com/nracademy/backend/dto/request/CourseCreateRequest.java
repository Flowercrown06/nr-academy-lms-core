package com.nracademy.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseCreateRequest {
    @NotBlank
    private String name;
    private String legalName;
    @Email
    private String contactEmail;
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
