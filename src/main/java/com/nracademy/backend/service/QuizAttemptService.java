package com.nracademy.backend.service;

import com.nracademy.backend.dto.request.SaveQuizAnswersRequest;
import com.nracademy.backend.dto.response.QuizAttemptResultDto;
import com.nracademy.backend.dto.response.StartQuizAttemptResponse;

import java.util.UUID;

/**
 * Student-facing quiz attempt lifecycle: start, save answers, submit, view result.
 *
 * Every method here is scoped to (courseId, current student) - a student
 * can only ever see or act on their OWN attempts, never another student's,
 * and never outside their tenant.
 */
public interface QuizAttemptService {
    StartQuizAttemptResponse startAttempt(UUID quizId);

    void saveAnswers(UUID attemptId, SaveQuizAnswersRequest request);

    QuizAttemptResultDto submitAttempt(UUID attemptId);

    QuizAttemptResultDto getResult(UUID attemptId);
}
