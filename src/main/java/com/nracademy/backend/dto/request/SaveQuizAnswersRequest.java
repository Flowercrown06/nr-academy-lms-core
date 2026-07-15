package com.nracademy.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class SaveQuizAnswersRequest {
    @NotEmpty
    @Valid
    private List<QuizAnswerRequest> answers;
}