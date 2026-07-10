package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

public class CourseNotFoundException extends AppException {
    public CourseNotFoundException() {
        super("Course was not found.", StatusCode.COURSE_NOT_FOUND, List.of(), HttpStatus.NOT_FOUND);
    }

    public CourseNotFoundException(UUID courseId) {
        super("Course was not found: " + courseId, StatusCode.COURSE_NOT_FOUND, List.of(), HttpStatus.NOT_FOUND);
    }
}
