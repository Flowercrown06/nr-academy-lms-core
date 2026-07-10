package com.nracademy.backend.dto.request;

import com.nracademy.backend.entity.enums.CourseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseStatusUpdateRequest {
    @NotNull
    private CourseStatus status;
    private String reason;
}
