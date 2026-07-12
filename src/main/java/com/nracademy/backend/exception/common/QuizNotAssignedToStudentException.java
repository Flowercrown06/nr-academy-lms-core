package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

public class QuizNotAssignedToStudentException extends QuizException {
    public QuizNotAssignedToStudentException() {
        super("You are not assigned to this quiz.", StatusCode.QUIZ_NOT_ASSIGNED_TO_STUDENT, HttpStatus.FORBIDDEN);
    }
}