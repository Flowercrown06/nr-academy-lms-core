package com.nracademy.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseCommercialTermsRequest {
    @NotNull
    private BigDecimal commissionRate;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal paymentPerStudent;
}
