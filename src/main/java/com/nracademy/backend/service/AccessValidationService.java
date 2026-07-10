package com.nracademy.backend.service;

import com.nracademy.backend.entity.course.Course;
import com.nracademy.backend.entity.user.User;

public interface AccessValidationService {
    void validateUserCanAuthenticate(User user);
    void validateUserCanAccess(User user);
    void validateCourseForUser(User user, Course course);
}
