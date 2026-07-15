package com.nracademy.backend.tenant;

import com.nracademy.backend.entity.Course;
import com.nracademy.backend.entity.enums.CourseStatus;
import com.nracademy.backend.entity.enums.Role;
import com.nracademy.backend.entity.User;
import com.nracademy.backend.exception.common.RoleForbiddenException;
import com.nracademy.backend.exception.common.TenantAccessDeniedException;
import com.nracademy.backend.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TenantGuard {

    private final CurrentUserService currentUserService;
    private final CourseRepository courseRepository;

    public User requireAuthenticatedUser() {
        return currentUserService.requireCurrentUser();
    }

    public void requireSuperAdmin() {
        User user = requireAuthenticatedUser();
        if (user.getRole() != Role.SUPER_ADMIN) {
            throw new RoleForbiddenException("You do not have permission to perform this operation.");
        }
    }

    public void requireCourseAccess(UUID courseId) {
        User user = requireAuthenticatedUser();
        if (user.getRole() == Role.SUPER_ADMIN) {
            return;
        }
        if (user.getCourseId() == null || !user.getCourseId().equals(courseId)) {
            throw new TenantAccessDeniedException();
        }
    }

    public UUID currentCourseId() {
        return requireAuthenticatedUser().getCourseId();
    }

    public boolean isSuperAdmin() {
        return requireAuthenticatedUser().getRole() == Role.SUPER_ADMIN;
    }

    public Course requireActiveCourse(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(com.nracademy.backend.exception.common.CourseNotFoundException::new);
        return course;
    }

    public void validateCourseOperational(Course course) {
        if (course.getStatus() == CourseStatus.INACTIVE) {
            throw new com.nracademy.backend.exception.common.CourseInactiveException();
        }
        if (course.getStatus() == CourseStatus.BLOCKED) {
            throw new com.nracademy.backend.exception.common.CourseBlockedException();
        }
    }
}
