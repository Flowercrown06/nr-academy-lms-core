package com.nracademy.backend.dto.response;

import com.nracademy.backend.entity.enums.CourseStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
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
    @Email
    String contactEmail;
    @Pattern(
            regexp = "^(\\+994|994|0)(50|51|55|70|77|99)\\d{7}$",
            message = "Invalid Azerbaijani phone number"
    )
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
