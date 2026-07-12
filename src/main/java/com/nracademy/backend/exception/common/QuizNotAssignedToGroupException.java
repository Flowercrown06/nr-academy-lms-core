package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

public class QuizNotAssignedToGroupException extends QuizException {
    public QuizNotAssignedToGroupException() {
        super("Quiz does not belong to the requested group.", StatusCode.QUIZ_NOT_ASSIGNED_TO_GROUP, HttpStatus.FORBIDDEN);
    }
}