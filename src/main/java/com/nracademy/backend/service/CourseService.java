package com.nracademy.backend.service;

import com.nracademy.backend.common.api.PageResponse;
import com.nracademy.backend.dto.request.CourseCommercialTermsRequest;
import com.nracademy.backend.dto.request.CourseCreateRequest;
import com.nracademy.backend.dto.request.CourseStatusUpdateRequest;
import com.nracademy.backend.dto.request.CourseUpdateRequest;
import com.nracademy.backend.dto.response.CourseCreateResponse;
import com.nracademy.backend.dto.response.CourseDto;
import com.nracademy.backend.entity.enums.CourseStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CourseService {
    PageResponse<CourseDto> listCourses(CourseStatus status, String q, Instant createdFrom, Instant createdTo,
                                        int page, int size, List<String> sort);
    CourseCreateResponse createCourse(CourseCreateRequest request);
    CourseDto getCourse(UUID courseId);
    CourseDto updateCourse(UUID courseId, CourseUpdateRequest request);
    CourseDto updateStatus(UUID courseId, CourseStatusUpdateRequest request);
    CourseDto updateCommercialTerms(UUID courseId, CourseCommercialTermsRequest request);
}
