package com.nracademy.backend.repository;

import com.nracademy.backend.entity.Group;
import com.nracademy.backend.entity.enums.GroupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID>, JpaSpecificationExecutor<Group> {

    boolean existsByCourseIdAndName(UUID courseId, String name);

    Page<Group> findByCourseId(UUID courseId, Pageable pageable);

    List<Group> findByTeacherIdAndStatus(UUID teacherId, GroupStatus status);
}
