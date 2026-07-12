package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

public class QuizAutoCompletionFailedException extends QuizAttemptException {
    public QuizAutoCompletionFailedException() {
        super("Scheduled attempt finalization failed.",
                StatusCode.QUIZ_AUTO_COMPLETION_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}