package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

public class QuizQuestionHasNoCorrectOptionException extends QuizException {
    public QuizQuestionHasNoCorrectOptionException() {
        super("Question must have at least one correct option.",
                StatusCode.QUIZ_QUESTION_HAS_NO_CORRECT_OPTION, HttpStatus.BAD_REQUEST);
    }
}