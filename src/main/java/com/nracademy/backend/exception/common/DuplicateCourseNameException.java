package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class DuplicateCourseNameException extends AppException {
    public DuplicateCourseNameException() {
        super("Course name is already taken.", StatusCode.DUPLICATE_COURSE_NAME, List.of(), HttpStatus.CONFLICT);
    }
}
