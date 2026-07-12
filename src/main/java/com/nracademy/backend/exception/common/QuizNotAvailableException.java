package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

public class QuizNotAvailableException extends QuizException {
    public QuizNotAvailableException() {
        super("Quiz is outside its availability window.", StatusCode.QUIZ_NOT_AVAILABLE, HttpStatus.CONFLICT);
    }
}