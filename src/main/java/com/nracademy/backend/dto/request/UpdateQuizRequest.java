package com.nracademy.backend.dto.request;

import com.nracademy.backend.entity.enums.QuizStatus;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateQuizRequest {
    private String title;

    @Positive
    private Integer durationMinutes;
    private LocalDateTime availableFrom;
    private LocalDateTime availableUntil;
    private QuizStatus status;
}