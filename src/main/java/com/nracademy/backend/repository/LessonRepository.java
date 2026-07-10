package com.nracademy.backend.repository;

import com.nracademy.backend.entity.enums.LessonStatus;
import com.nracademy.backend.entity.lesson.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID>, JpaSpecificationExecutor<Lesson> {

    List<Lesson> findByCourseIdAndGroupIdOrderByLessonDateAsc(UUID courseId, UUID groupId);

    boolean existsByCourseIdAndGroupIdAndStatus(UUID courseId, UUID groupId, LessonStatus status);
}
