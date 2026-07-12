package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

/**
 * Thrown defensively if a code path ever tries to serialize QuizOption.correct
 * into a student-facing response. Should never happen in normal operation;
 * this is a last line of defense, not expected user-facing behavior.
 */
public class QuizCorrectOptionExposedException extends QuizException {
    public QuizCorrectOptionExposedException() {
        super("Internal error: correct option flag would have been exposed to a student.",
                StatusCode.QUIZ_CORRECT_OPTION_EXPOSED, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}