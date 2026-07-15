package com.nracademy.backend.entity;

import com.nracademy.backend.entity.enums.BillingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
        name = "monthly_course_billing",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_billing_course_year_month",
                columnNames = {"course_id", "billing_year", "billing_month"}
        )
)
public class MonthlyCourseBilling {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "course_id", nullable = false)
    UUID courseId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    Course course;

    @Column(name = "billing_year", nullable = false)
    Integer billingYear;

    @Column(name = "billing_month", nullable = false)
    Integer billingMonth;

    @Column(name = "active_student_count", nullable = false)
    Integer activeStudentCount;

    @Column(name = "payment_per_student", nullable = false, precision = 12, scale = 2)
    BigDecimal paymentPerStudent;

    @Column(name = "commission_rate", nullable = false, precision = 5, scale = 2)
    BigDecimal commissionRate;

    @Column(name = "gross_base_amount", nullable = false, precision = 14, scale = 2)
    BigDecimal grossBaseAmount;

    @Column(name = "amount_due", nullable = false, precision = 14, scale = 2)
    BigDecimal amountDue;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    BillingStatus status = BillingStatus.GENERATED;

    @Column(name = "generated_at", nullable = false, updatable = false)
    Instant generatedAt;

    @PrePersist
    void prePersist() {
        if (generatedAt == null) {
            generatedAt = Instant.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MonthlyCourseBilling other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
