package com.nracademy.backend.dto.response;

import com.nracademy.backend.entity.enums.QuizAttemptStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptResultDto {
    UUID attemptId;
    QuizAttemptStatus status;
    BigDecimal score;
    Integer correctCount;
    Integer totalQuestions;
    LocalDateTime submittedAt;
}