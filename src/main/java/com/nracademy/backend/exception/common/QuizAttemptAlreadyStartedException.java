package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

public class QuizAttemptAlreadyStartedException extends QuizAttemptException {
    public QuizAttemptAlreadyStartedException() {
        super("You already have an attempt in progress for this quiz.",
                StatusCode.QUIZ_ATTEMPT_ALREADY_STARTED, HttpStatus.CONFLICT);
    }
}