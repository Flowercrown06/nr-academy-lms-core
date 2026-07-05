package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import com.nracademy.backend.exception.AppException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class CourseInactiveException extends AppException {
    public CourseInactiveException() {
        super("This course is currently inactive.",
            StatusCode.COURSE_INACTIVE, List.of(), HttpStatus.FORBIDDEN);
    }
}
