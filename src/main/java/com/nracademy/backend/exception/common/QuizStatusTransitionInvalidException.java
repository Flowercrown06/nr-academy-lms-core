package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

public class QuizStatusTransitionInvalidException extends QuizException {
    public QuizStatusTransitionInvalidException(String from, String to) {
        super("Cannot transition quiz status from " + from + " to " + to + ".",
                StatusCode.QUIZ_STATUS_TRANSITION_INVALID, HttpStatus.CONFLICT);
    }
}