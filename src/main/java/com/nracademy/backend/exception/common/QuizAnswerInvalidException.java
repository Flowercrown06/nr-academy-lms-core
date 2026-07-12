package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

public class QuizAnswerInvalidException extends QuizAttemptException {
    public QuizAnswerInvalidException() {
        super("Answer payload is invalid.", StatusCode.QUIZ_ANSWER_INVALID, HttpStatus.BAD_REQUEST);
    }

    public QuizAnswerInvalidException(String reason) {
        super("Answer payload is invalid: " + reason, StatusCode.QUIZ_ANSWER_INVALID, HttpStatus.BAD_REQUEST);
    }
}