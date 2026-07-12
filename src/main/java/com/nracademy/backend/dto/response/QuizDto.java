package com.nracademy.backend.dto.response;

import com.nracademy.backend.entity.enums.QuizStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class QuizDto {
    UUID id;
    UUID courseId;
    UUID groupId;
    UUID teacherId;
    String title;
    Integer durationMinutes;
    LocalDateTime availableFrom;
    LocalDateTime availableUntil;
    QuizStatus status;
    int questionCount;
}