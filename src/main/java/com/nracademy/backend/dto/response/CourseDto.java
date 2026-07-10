package com.nracademy.backend.dto.response;

import com.nracademy.backend.entity.enums.CourseStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class CourseDto {
    UUID id;
    String name;
    String legalName;
    String contactEmail;
    String contactPhone;
    String logoUrl;
    String primaryColor;
    String secondaryColor;
    CourseStatus status;
    BigDecimal commissionRate;
    BigDecimal paymentPerStudent;
    Instant createdAt;
    Instant updatedAt;
}
