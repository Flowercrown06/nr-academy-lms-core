package com.nracademy.backend.service.impl;

import com.nracademy.backend.dto.response.DashboardSummaryResponse;
import com.nracademy.backend.entity.enums.CourseStatus;
import com.nracademy.backend.entity.enums.Role;
import com.nracademy.backend.entity.enums.UserStatus;
import com.nracademy.backend.repository.CourseRepository;
import com.nracademy.backend.repository.QuizRepository;
import com.nracademy.backend.repository.UserRepository;
import com.nracademy.backend.service.DashboardService;
import com.nracademy.backend.tenant.TenantGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TenantGuard tenantGuard;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSuperAdminSummary() {
        tenantGuard.requireSuperAdmin();
        return DashboardSummaryResponse.builder()
                .activeCourses(courseRepository.countByStatus(CourseStatus.ACTIVE))
                .blockedCourses(courseRepository.countByStatus(CourseStatus.BLOCKED))
                .totalTeachers(userRepository.countByRoleAndStatus(Role.TEACHER, UserStatus.ACTIVE))
                .totalStudents(userRepository.countByRoleAndStatus(Role.STUDENT, UserStatus.ACTIVE))
                .totalQuizzes(quizRepository.count())
                .inactiveUsers(userRepository.countByStatus(UserStatus.INACTIVE))
                .build();
    }
}
