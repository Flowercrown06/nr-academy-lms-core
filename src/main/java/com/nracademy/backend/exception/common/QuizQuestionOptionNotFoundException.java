package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class QuizQuestionOptionNotFoundException extends QuizException {
    public QuizQuestionOptionNotFoundException() {
        super("Quiz option was not found.", StatusCode.QUIZ_QUESTION_OPTION_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    public QuizQuestionOptionNotFoundException(UUID optionId) {
        super("Quiz option was not found: " + optionId, StatusCode.QUIZ_QUESTION_OPTION_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}