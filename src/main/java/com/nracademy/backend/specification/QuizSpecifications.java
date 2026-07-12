package com.nracademy.backend.specification;

import com.nracademy.backend.entity.enums.QuizStatus;
import com.nracademy.backend.entity.quiz.Quiz;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Filter predicates for GET /api/v1/quizzes (doc 8.13 / filter table row for
 * quizzes). Every predicate here is combined with AND; courseId scoping is
 * applied separately by the caller (QuizService) since it's a security
 * boundary, not just a filter, and must never be optional.
 */
public final class QuizSpecifications {

    private QuizSpecifications() {
    }

    public static Specification<Quiz> withFilters(
            UUID courseId,
            UUID groupId,
            QuizStatus status,
            String q,
            LocalDateTime availableFrom,
            LocalDateTime availableTo,
            LocalDateTime createdFrom,
            LocalDateTime createdTo) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Tenant scope - always applied, never conditional.
            predicates.add(cb.equal(root.get("courseId"), courseId));

            if (groupId != null) {
                predicates.add(cb.equal(root.get("groupId"), groupId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (q != null && q.length() >= 2) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + q.toLowerCase() + "%"));
            }
            if (availableFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("availableFrom"), availableFrom));
            }
            if (availableTo != null) {
                predicates.add(cb.lessThan(root.get("availableUntil"), availableTo));
            }
            if (createdFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("createdAt"), createdFrom.atZone(ZoneId.systemDefault()).toInstant()));
            }
            if (createdTo != null) {
                predicates.add(cb.lessThan(
                        root.get("createdAt"), createdTo.atZone(ZoneId.systemDefault()).toInstant()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * GET /api/v1/student/quizzes (doc 8.14 / filter table row for student
     * quizzes). Restricted to PUBLISHED or CLOSED quizzes only - a student
     * must never see a DRAFT quiz, regardless of group membership - plus
     * whichever groups the student currently belongs to.
     */
    public static Specification<Quiz> forStudent(
            UUID courseId,
            List<UUID> studentGroupIds,
            QuizStatus status,
            String q,
            LocalDateTime availableFrom,
            LocalDateTime availableTo) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("courseId"), courseId));
            predicates.add(root.get("groupId").in(studentGroupIds));
            predicates.add(root.get("status").in(QuizStatus.PUBLISHED, QuizStatus.CLOSED));

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (q != null && q.length() >= 2) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + q.toLowerCase() + "%"));
            }
            if (availableFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("availableFrom"), availableFrom));
            }
            if (availableTo != null) {
                predicates.add(cb.lessThan(root.get("availableUntil"), availableTo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
