package com.nracademy.backend.dto.request;

import com.nracademy.backend.entity.enums.QuizStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateQuizRequest {
    @NotNull
    private UUID groupId;

    @jakarta.validation.constraints.NotBlank
    private String title;

    @NotNull
    @Positive
    private Integer durationMinutes;

    private LocalDateTime availableFrom;
    private LocalDateTime availableUntil;

    private QuizStatus status;
}