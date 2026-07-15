package com.nracademy.backend.service.impl;

import com.nracademy.backend.common.api.PageResponse;
import com.nracademy.backend.dto.request.CourseCommercialTermsRequest;
import com.nracademy.backend.dto.request.CourseCreateRequest;
import com.nracademy.backend.dto.request.CourseStatusUpdateRequest;
import com.nracademy.backend.dto.request.CourseUpdateRequest;
import com.nracademy.backend.dto.response.CourseCreateResponse;
import com.nracademy.backend.dto.response.CourseDto;
import com.nracademy.backend.entity.Course;
import com.nracademy.backend.entity.enums.CourseStatus;
import com.nracademy.backend.entity.enums.Role;
import com.nracademy.backend.entity.enums.UserStatus;
import com.nracademy.backend.entity.User;
import com.nracademy.backend.exception.common.CourseNotFoundException;
import com.nracademy.backend.exception.common.DuplicateCourseNameException;
import com.nracademy.backend.exception.common.EmailAlreadyRegisteredException;
import com.nracademy.backend.exception.common.InvalidCommissionRateException;
import com.nracademy.backend.exception.common.InvalidPaymentPerStudentException;
import com.nracademy.backend.exception.common.SearchQueryTooShortException;
import com.nracademy.backend.mapper.CourseMapper;
import com.nracademy.backend.repository.CourseRepository;
import com.nracademy.backend.repository.UserRepository;
import com.nracademy.backend.service.CourseService;
import com.nracademy.backend.specification.CourseSpecifications;
import com.nracademy.backend.tenant.TenantGuard;
import com.nracademy.backend.util.PageRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private static final Set<String> ALLOWED_SORT = Set.of(
            "name", "status", "createdAt", "updatedAt", "commissionRate", "paymentPerStudent"
    );

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseMapper courseMapper;
    private final TenantGuard tenantGuard;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CourseDto> listCourses(
            CourseStatus status, String q, Instant createdFrom, Instant createdTo,
            int page, int size, List<String> sort) {
        tenantGuard.requireSuperAdmin();
        validateSearchQuery(q);
        Pageable pageable = PageRequestUtil.create(page, size, sort, ALLOWED_SORT, "createdAt,desc");
        Specification<Course> spec = CourseSpecifications.withFilters(status, trimQuery(q), createdFrom, createdTo);
        Page<CourseDto> result = courseRepository.findAll(spec, pageable).map(courseMapper::toDto);
        return PageResponse.from(result, PageRequestUtil.resolvedSort(sort, "createdAt,desc"));
    }

    @Override
    @Transactional
    public CourseCreateResponse createCourse(CourseCreateRequest request) {
        tenantGuard.requireSuperAdmin();
        validateCommercialTerms(request.getCommissionRate(), request.getPaymentPerStudent());
        if (courseRepository.existsByName(request.getName())) {
            throw new DuplicateCourseNameException();
        }
        if (userRepository.existsByEmail(request.getOwnerEmail())) {
            throw new EmailAlreadyRegisteredException(
                    "Email already registered", com.nracademy.backend.entity.enums.StatusCode.EMAIL_ALREADY_REGISTERED, List.of());
        }

        Course course = Course.builder()
                .name(request.getName())
                .legalName(request.getLegalName())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .status(CourseStatus.ACTIVE)
                .commissionRate(request.getCommissionRate())
                .paymentPerStudent(request.getPaymentPerStudent())
                .build();
        course = courseRepository.save(course);

        User owner = User.builder()
                .courseId(course.getId())
                .name(request.getOwnerName())
                .surname(request.getOwnerSurname())
                .email(request.getOwnerEmail())
                .passwordHash(passwordEncoder.encode(request.getOwnerPassword()))
                .role(Role.COURSE_OWNER)
                .status(UserStatus.ACTIVE)
                .build();
        owner = userRepository.save(owner);

        return CourseCreateResponse.builder()
                .courseId(course.getId())
                .ownerUserId(owner.getId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDto getCourse(UUID courseId) {
        tenantGuard.requireSuperAdmin();
        return courseMapper.toDto(findCourse(courseId));
    }

    @Override
    @Transactional
    public CourseDto updateCourse(UUID courseId, CourseUpdateRequest request) {
        tenantGuard.requireSuperAdmin();
        Course course = findCourse(courseId);
        if (request.getName() != null) {
            course.setName(request.getName());
        }
        if (request.getLegalName() != null) {
            course.setLegalName(request.getLegalName());
        }
        if (request.getContactEmail() != null) {
            course.setContactEmail(request.getContactEmail());
        }
        if (request.getContactPhone() != null) {
            course.setContactPhone(request.getContactPhone());
        }
        return courseMapper.toDto(courseRepository.save(course));
    }

    @Override
    @Transactional
    public CourseDto updateStatus(UUID courseId, CourseStatusUpdateRequest request) {
        tenantGuard.requireSuperAdmin();
        Course course = findCourse(courseId);
        course.setStatus(request.getStatus());
        return courseMapper.toDto(courseRepository.save(course));
    }

    @Override
    @Transactional
    public CourseDto updateCommercialTerms(UUID courseId, CourseCommercialTermsRequest request) {
        tenantGuard.requireSuperAdmin();
        validateCommercialTerms(request.getCommissionRate(), request.getPaymentPerStudent());
        Course course = findCourse(courseId);
        course.setCommissionRate(request.getCommissionRate());
        course.setPaymentPerStudent(request.getPaymentPerStudent());
        return courseMapper.toDto(courseRepository.save(course));
    }

    private Course findCourse(UUID courseId) {
        return courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException(courseId));
    }

    private void validateCommercialTerms(BigDecimal commissionRate, BigDecimal paymentPerStudent) {
        if (commissionRate == null || commissionRate.compareTo(BigDecimal.ZERO) < 0
                || commissionRate.compareTo(new BigDecimal("100")) > 0) {
            throw new InvalidCommissionRateException();
        }
        if (paymentPerStudent == null || paymentPerStudent.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidPaymentPerStudentException();
        }
    }

    private void validateSearchQuery(String q)  {
        if (q != null && !q.isBlank() && q.trim().length() < 2) {
            throw new SearchQueryTooShortException(2);
        }
    }

    private String trimQuery(String q) {
        if (q == null) {
            return null;
        }
        String trimmed = q.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
