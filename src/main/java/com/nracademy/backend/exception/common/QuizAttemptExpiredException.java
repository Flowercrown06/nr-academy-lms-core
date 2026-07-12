package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

public class QuizAttemptExpiredException extends QuizAttemptException {
    public QuizAttemptExpiredException() {
        super("Quiz attempt time has expired.", StatusCode.QUIZ_ATTEMPT_EXPIRED, HttpStatus.CONFLICT);
    }
}