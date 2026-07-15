package com.nracademy.backend.exception.common;

import com.nracademy.backend.dto.error.ErrorDetailDTO;
import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

/** Base exception for quiz/question/option errors that are not attempt-specific. */
public class QuizException extends AppException {
    public QuizException(String message, StatusCode statusCode, HttpStatus httpStatus) {
        super(message, statusCode, List.<ErrorDetailDTO>of(), httpStatus);
    }
}