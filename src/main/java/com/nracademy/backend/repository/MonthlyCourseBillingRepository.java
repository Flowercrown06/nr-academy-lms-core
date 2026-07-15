package com.nracademy.backend.repository;

import com.nracademy.backend.entity.MonthlyCourseBilling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MonthlyCourseBillingRepository extends JpaRepository<MonthlyCourseBilling, UUID>,
        JpaSpecificationExecutor<MonthlyCourseBilling> {

    boolean existsByCourseIdAndBillingYearAndBillingMonth(UUID courseId, int year, int month);

    List<MonthlyCourseBilling> findByCourseIdOrderByBillingYearDescBillingMonthDesc(UUID courseId);
}
