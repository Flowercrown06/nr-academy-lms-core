package com.nracademy.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartQuizAttemptResponse {
    UUID attemptId;
    UUID quizId;
    LocalDateTime startedAt;
    LocalDateTime expiresAt;
    List<StudentQuestionDto> questions;
}