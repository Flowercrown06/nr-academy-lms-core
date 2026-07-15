package com.nracademy.backend.dto.response;

import com.nracademy.backend.entity.enums.QuizAttemptStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class QuizAttemptResultDto {
    UUID attemptId;
    QuizAttemptStatus status;
    BigDecimal score;
    Integer correctCount;
    Integer totalQuestions;
    LocalDateTime submittedAt;
}