package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

public class QuizAttemptLimitExceededException extends QuizAttemptException {
    public QuizAttemptLimitExceededException() {
        super("You have used all allowed attempts for this quiz.",
                StatusCode.QUIZ_ATTEMPT_LIMIT_EXCEEDED, HttpStatus.CONFLICT);
    }
}