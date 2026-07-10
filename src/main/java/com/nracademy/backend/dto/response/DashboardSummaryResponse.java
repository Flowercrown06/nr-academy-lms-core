package com.nracademy.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardSummaryResponse {
    long activeCourses;
    long totalTeachers;
    long totalStudents;
    long totalQuizzes;
    long blockedCourses;
    long inactiveUsers;
}
