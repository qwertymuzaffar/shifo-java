package com.shifo.shifo_java.features.payment;

import com.shifo.shifo_java.common.enums.AppointmentType;
import com.shifo.shifo_java.common.enums.PaymentMethod;
import com.shifo.shifo_java.common.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByAppointmentId(Long appointmentId);

    @Query("""
        SELECT p FROM Payment p
        JOIN FETCH p.appointment a
        JOIN FETCH a.patient
        JOIN FETCH a.doctor
        WHERE a.id = :appointmentId
    """)
    Optional<Payment> findByAppointmentIdWithRelations(Long appointmentId);

    @Modifying
    @Query("UPDATE Payment p SET p.isActive = true WHERE p.id = :id")
    void softDelete(Long id);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'PAID'")
    BigDecimal getTotalPaidAmount();

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.status = 'PAID'
        AND p.paidAt BETWEEN :start AND :end
    """)
    BigDecimal sumPaidBetween(@Param("start") Instant start, @Param("end") Instant end);

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.status = 'PAID'
        AND p.paymentType = :paymentType
        AND p.paidAt BETWEEN :start AND :end
    """)
    BigDecimal sumPaidByType(@Param("paymentType") String type,
                             @Param("start") Instant start,
                             @Param("end") Instant end);

    long countByPaymentTypeAndStatusAndPaidAtBetween(String type, PaymentStatus status,
                                                     Instant start, Instant end);

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.status = com.shifo.shifo_java.common.enums.PaymentStatus.PAID
        """)
    BigDecimal sumTotalPaidAmount();

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.status = com.shifo.shifo_java.common.enums.PaymentStatus.PAID
          AND p.paidAt BETWEEN :start AND :end
          AND (:doctorIds IS NULL OR p.appointment.doctor.id IN :doctorIds)
          AND (:types IS NULL OR p.appointment.type IN :types)
        """)
    BigDecimal sumPaidAmountByDateRangeAndFilters(
            @Param("start") Instant start,
            @Param("end") Instant end,
            @Param("doctorIds") List<Long> doctorIds,
            @Param("types") List<AppointmentType> types
    );

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.status = com.shifo.shifo_java.common.enums.PaymentStatus.PAID
          AND p.paidAt BETWEEN :start AND :end
          AND p.paymentMethod = :paymentMethod
          AND (:doctorIds IS NULL OR p.appointment.doctor.id IN :doctorIds)
          AND (:types IS NULL OR p.appointment.type IN :types)
        """)
    BigDecimal sumPaidAmountByTypeAndDateRange(
            @Param("start") Instant start,
            @Param("end") Instant end,
            @Param("paymentMethod") PaymentMethod paymentMethod,
            @Param("doctorIds") List<Long> doctorIds,
            @Param("types") List<AppointmentType> types
    );

    @Query("""
        SELECT COUNT(p)
        FROM Payment p
        WHERE p.status = com.shifo.shifo_java.common.enums.PaymentStatus.PAID
          AND p.paidAt BETWEEN :start AND :end
          AND p.paymentMethod = :paymentMethod
          AND (:doctorIds IS NULL OR p.appointment.doctor.id IN :doctorIds)
          AND (:types IS NULL OR p.appointment.type IN :types)
        """)
    long countPaidByTypeAndDateRange(
            @Param("start") Instant start,
            @Param("end") Instant end,
            @Param("paymentMethod") PaymentMethod paymentMethod,
            @Param("doctorIds") List<Long> doctorIds,
            @Param("types") List<AppointmentType> types
    );
}
