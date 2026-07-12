package com.nracademy.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class QuizAnswerRequest {
    @NotNull
    private UUID questionId;

    private UUID selectedOptionId;
}