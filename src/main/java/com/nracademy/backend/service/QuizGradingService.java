package com.nracademy.backend.service;

import com.nracademy.backend.entity.enums.QuizAttemptStatus;
import com.nracademy.backend.entity.quiz.QuizAttempt;
import com.nracademy.backend.entity.quiz.QuizQuestion;

import java.time.LocalDateTime;
import java.util.List;

public interface QuizGradingService {

    void grade(QuizAttempt attempt,
               List<QuizQuestion> questions,
               QuizAttemptStatus finalStatus,
               LocalDateTime submittedAt);
}
