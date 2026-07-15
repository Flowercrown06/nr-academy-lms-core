package com.nracademy.backend.dto.request;

import com.nracademy.backend.entity.enums.QuizStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizStatusUpdateRequest {
    @NotNull
    private QuizStatus status;
}