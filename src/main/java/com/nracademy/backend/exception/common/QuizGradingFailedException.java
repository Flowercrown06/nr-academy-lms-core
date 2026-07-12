package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

public class QuizGradingFailedException extends QuizAttemptException {
    public QuizGradingFailedException() {
        super("Grading algorithm failed unexpectedly.",
                StatusCode.QUIZ_GRADING_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}