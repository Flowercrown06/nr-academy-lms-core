package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class QuizQuestionNotFoundException extends QuizException {
    public QuizQuestionNotFoundException() {
        super("Quiz question was not found.", StatusCode.QUIZ_QUESTION_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    public QuizQuestionNotFoundException(UUID questionId) {
        super("Quiz question was not found: " + questionId, StatusCode.QUIZ_QUESTION_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}