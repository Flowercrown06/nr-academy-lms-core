package com.nracademy.backend.service;

import com.nracademy.backend.dto.request.SaveQuizAnswersRequest;
import com.nracademy.backend.dto.response.QuizAttemptResultDto;
import com.nracademy.backend.dto.response.QuizDto;
import com.nracademy.backend.dto.response.StartQuizAttemptResponse;
import com.nracademy.backend.entity.enums.QuizStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface QuizAttemptService {

    Page<QuizDto> listAssignedQuizzes(
            QuizStatus status,
            String q,
            LocalDateTime availableFrom,
            LocalDateTime availableTo,
            Boolean attempted,
            Pageable pageable
    );

    StartQuizAttemptResponse startAttempt(UUID quizId);

    void saveAnswers(UUID attemptId, SaveQuizAnswersRequest request);

    QuizAttemptResultDto submitAttempt(UUID attemptId);

    QuizAttemptResultDto getResult(UUID attemptId);
}