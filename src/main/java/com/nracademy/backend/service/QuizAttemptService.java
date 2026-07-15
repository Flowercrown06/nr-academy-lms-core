package com.nracademy.backend.service;

import com.nracademy.backend.dto.request.SaveQuizAnswersRequest;
import com.nracademy.backend.dto.response.QuizAttemptResultDto;
import com.nracademy.backend.dto.response.StartQuizAttemptResponse;

import java.util.UUID;

public interface QuizAttemptService {
    StartQuizAttemptResponse startAttempt(UUID quizId);

    void saveAnswers(UUID attemptId, SaveQuizAnswersRequest request);

    QuizAttemptResultDto submitAttempt(UUID attemptId);

    QuizAttemptResultDto getResult(UUID attemptId);
}
