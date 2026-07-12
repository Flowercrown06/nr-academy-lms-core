package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class QuizAttemptNotFoundException extends QuizAttemptException {
    public QuizAttemptNotFoundException() {
        super("Quiz attempt was not found.", StatusCode.QUIZ_ATTEMPT_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    public QuizAttemptNotFoundException(UUID attemptId) {
        super("Quiz attempt was not found: " + attemptId, StatusCode.QUIZ_ATTEMPT_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}