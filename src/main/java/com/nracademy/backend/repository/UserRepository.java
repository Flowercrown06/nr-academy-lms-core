package com.nracademy.backend.repository;

import com.nracademy.backend.entity.enums.Role;
import com.nracademy.backend.entity.enums.UserStatus;
import com.nracademy.backend.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("select u.id from User u where u.status = :status")
    List<UUID> findIdsByStatus(@Param("status") UserStatus status);

    Page<User> findAllByStatus(UserStatus status, Pageable pageable);

    Page<User> findAllByCreatedAtBetween(Instant start, Instant end, Pageable pageable);

    long countByStatus(UserStatus status);

    long countByCourseIdAndRoleAndStatus(UUID courseId, Role role, UserStatus status);

    long countByRoleAndStatus(Role role, UserStatus status);

    boolean existsByPhone(String phone);

    boolean existsByEmailAndIdNot(String email, UUID id);

    boolean existsByPhoneAndIdNot(String phone, UUID id);

    List<User> findByCourseIdAndRole(UUID courseId, Role role);
}
