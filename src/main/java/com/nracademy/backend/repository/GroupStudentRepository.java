package com.nracademy.backend.repository;

import com.nracademy.backend.entity.GroupStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupStudentRepository extends JpaRepository<GroupStudent, UUID> {

    Optional<GroupStudent> findByCourseIdAndGroupIdAndStudentId(UUID courseId, UUID groupId, UUID studentId);

    List<GroupStudent> findByCourseIdAndGroupIdAndActiveTrue(UUID courseId, UUID groupId);

    List<GroupStudent> findByCourseIdAndStudentIdAndActiveTrue(UUID courseId, UUID studentId);

    boolean existsByCourseIdAndGroupIdAndStudentId(UUID courseId, UUID groupId, UUID studentId);
}
