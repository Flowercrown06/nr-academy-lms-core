package com.nracademy.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CourseCreateResponse {
    UUID courseId;
    UUID ownerUserId;
}
