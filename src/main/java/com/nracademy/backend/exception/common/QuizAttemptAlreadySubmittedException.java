package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

public class QuizAttemptAlreadySubmittedException extends QuizAttemptException {
    public QuizAttemptAlreadySubmittedException() {
        super("This quiz attempt has already been submitted.",
                StatusCode.QUIZ_ATTEMPT_ALREADY_SUBMITTED, HttpStatus.CONFLICT);
    }
}