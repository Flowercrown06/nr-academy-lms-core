package com.nracademy.backend.repository;

import com.nracademy.backend.entity.Course;
import com.nracademy.backend.entity.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID>, JpaSpecificationExecutor<Course> {

    boolean existsByName(String name);

    long countByStatus(CourseStatus status);
}
