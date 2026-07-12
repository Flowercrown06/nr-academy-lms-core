package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

public class QuizAttemptNotInProgressException extends QuizAttemptException {
    public QuizAttemptNotInProgressException() {
        super("Quiz attempt is not in a writable state.",
                StatusCode.QUIZ_ATTEMPT_NOT_IN_PROGRESS, HttpStatus.CONFLICT);
    }
}