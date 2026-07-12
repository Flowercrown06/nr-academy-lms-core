package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

public class QuizAnswerQuestionMismatchException extends QuizAttemptException {
    public QuizAnswerQuestionMismatchException() {
        super("Answer does not belong to this attempt's quiz question.",
                StatusCode.QUIZ_ANSWER_QUESTION_MISMATCH, HttpStatus.BAD_REQUEST);
    }
}