package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

public class QuizDurationInvalidException extends QuizException {
    public QuizDurationInvalidException() {
        super("Quiz duration must be a positive number of minutes within the allowed limit.",
                StatusCode.QUIZ_DURATION_INVALID, HttpStatus.BAD_REQUEST);
    }
}