package com.nracademy.backend.dto.request;

import com.nracademy.backend.entity.enums.QuizStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuizStatusUpdateRequest {
    @NotNull
    private QuizStatus status;
}