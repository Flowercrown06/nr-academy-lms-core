package com.nracademy.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nracademy.backend.entity.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query(
            """
                        SELECT u FROM User u 
                        LEFT JOIN FETCH u.roles 
                        WHERE u.email = :email
                    """)
    Optional<User> findByEmail(@Param("email") String email);

    boolean existsByEmail(String email);

    @Query("select u.id from User u where u.isActive = true")
    List<UUID> findIdsByIsActiveTrue();

    Page<User> findAllByIsActive(boolean isActive, Pageable pageable);

    Page<User> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    long countByIsActive(boolean isActive);

    boolean existsByPhone(String phone);

    boolean existsByEmailAndIdNot(String email, UUID id);

    boolean existsByPhoneAndIdNot(String phone, UUID id);
}