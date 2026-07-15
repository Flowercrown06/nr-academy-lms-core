package com.nracademy.backend.repository;

import com.nracademy.backend.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, UUID> {

    Optional<AttendanceRecord> findByCourseIdAndLessonIdAndStudentId(UUID courseId, UUID lessonId, UUID studentId);

    List<AttendanceRecord> findByCourseIdAndStudentId(UUID courseId, UUID studentId);

    List<AttendanceRecord> findByCourseIdAndLessonId(UUID courseId, UUID lessonId);

    long countByCourseIdAndStudentIdAndPresent(UUID courseId, UUID studentId, boolean present);
}
