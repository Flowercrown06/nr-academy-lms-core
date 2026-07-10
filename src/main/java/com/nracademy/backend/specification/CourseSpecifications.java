package com.nracademy.backend.specification;

import com.nracademy.backend.entity.course.Course;
import com.nracademy.backend.entity.enums.CourseStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class CourseSpecifications {

    private CourseSpecifications() {
    }

    public static Specification<Course> withFilters(CourseStatus status, String q, Instant createdFrom, Instant createdTo) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (q != null && q.length() >= 2) {
                String pattern = "%" + q.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("legalName")), pattern),
                        cb.like(cb.lower(root.get("contactEmail")), pattern),
                        cb.like(cb.lower(root.get("contactPhone")), pattern)
                ));
            }
            if (createdFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdFrom));
            }
            if (createdTo != null) {
                predicates.add(cb.lessThan(root.get("createdAt"), createdTo));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
