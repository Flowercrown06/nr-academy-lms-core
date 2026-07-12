package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

public class QuizNotPublishedException extends QuizException {
    public QuizNotPublishedException() {
        super("Quiz is not published.", StatusCode.QUIZ_NOT_PUBLISHED, HttpStatus.CONFLICT);
    }
}