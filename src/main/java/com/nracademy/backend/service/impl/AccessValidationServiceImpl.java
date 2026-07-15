package com.nracademy.backend.service.impl;

import com.nracademy.backend.entity.Course;
import com.nracademy.backend.entity.enums.CourseStatus;
import com.nracademy.backend.entity.enums.UserStatus;
import com.nracademy.backend.entity.User;
import com.nracademy.backend.exception.common.CourseBlockedException;
import com.nracademy.backend.exception.common.CourseInactiveException;
import com.nracademy.backend.exception.common.UserBlockedException;
import com.nracademy.backend.exception.common.UserInactiveException;
import com.nracademy.backend.repository.CourseRepository;
import com.nracademy.backend.service.AccessValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccessValidationServiceImpl implements AccessValidationService {

    private final CourseRepository courseRepository;

    @Override
    public void validateUserCanAuthenticate(User user) {
        validateUserStatus(user);
        validateTenantCourse(user);
    }

    @Override
    public void validateUserCanAccess(User user) {
        validateUserStatus(user);
        validateTenantCourse(user);
    }

    @Override
    public void validateCourseForUser(User user, Course course) {
        if (course.getStatus() == CourseStatus.INACTIVE) {
            throw new CourseInactiveException();
        }
        if (course.getStatus() == CourseStatus.BLOCKED) {
            throw new CourseBlockedException();
        }
    }

    private void validateUserStatus(User user) {
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new UserInactiveException();
        }
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new UserBlockedException();
        }
    }

    private void validateTenantCourse(User user) {
        UUID courseId = user.getCourseId();
        if (courseId == null) {
            return;
        }
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return;
        }
        validateCourseForUser(user, course);
    }
}
