package com.shifo.shifo_java.features.payment.repository;

import com.shifo.shifo_java.features.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
                SELECT p
                FROM Payment p
                WHERE p.appointment.id = :appointmentId
            """)
    List<Payment> findByAppointmentId(Long appointmentId);
}
