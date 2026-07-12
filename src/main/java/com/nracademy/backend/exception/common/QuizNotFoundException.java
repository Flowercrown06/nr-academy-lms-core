package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class QuizNotFoundException extends QuizException {
    public QuizNotFoundException() {
        super("Quiz was not found.", StatusCode.QUIZ_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    public QuizNotFoundException(UUID quizId) {
        super("Quiz was not found: " + quizId, StatusCode.QUIZ_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}