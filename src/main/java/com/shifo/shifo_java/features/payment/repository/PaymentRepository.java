package com.shifo.shifo_java.features.payment.repository;

import com.shifo.shifo_java.features.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
                SELECT p
                FROM Payment p
                WHERE p.appointment.id = :appointmentId
            """)
    List<Payment> findByAppointmentId(Long appointmentId);


    @Query("""
                SELECT p
                FROM Payment p
                LEFT JOIN FETCH p.appointment a
                LEFT JOIN FETCH p.patient pt
                WHERE p.id = :id
            """)
    Optional<Payment> findByIdWithRelations(Long id);
}
