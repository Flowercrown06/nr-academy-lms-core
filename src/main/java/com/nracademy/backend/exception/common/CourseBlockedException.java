package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class CourseBlockedException extends AppException {
    public CourseBlockedException() {
        super("Your account activity has been temporarily suspended. Please contact administration.",
            StatusCode.COURSE_BLOCKED, List.of(), HttpStatus.FORBIDDEN);
    }
}
