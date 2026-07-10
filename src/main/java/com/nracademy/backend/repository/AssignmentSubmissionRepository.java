package com.nracademy.backend.repository;

import com.nracademy.backend.entity.assignment.AssignmentSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, UUID> {

    Page<AssignmentSubmission> findByCourseIdAndStudentId(UUID courseId, UUID studentId, Pageable pageable);

    Optional<AssignmentSubmission> findByCourseIdAndAssignmentIdAndStudentId(
            UUID courseId, UUID assignmentId, UUID studentId);
}
