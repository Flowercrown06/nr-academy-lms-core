package com.nracademy.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerRequest {
    @NotNull
    private UUID questionId;

    // Nullable: a student may clear/skip an answer by sending selectedOptionId = null.
    private UUID selectedOptionId;
}