package com.nracademy.backend.exception.common;

import com.nracademy.backend.dto.error.ErrorDetailDTO;
import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

/** Base exception for quiz attempt / answer / submission errors. */
public class QuizAttemptException extends AppException {
    public QuizAttemptException(String message, StatusCode statusCode, HttpStatus httpStatus) {
        super(message, statusCode, List.<ErrorDetailDTO>of(), httpStatus);
    }
}