package com.nracademy.backend.dto.request;

import com.nracademy.backend.entity.enums.QuizStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    // Optional on create; defaults to DRAFT in the service if omitted.
    private QuizStatus status;
}