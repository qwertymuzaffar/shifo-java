package com.shifo.shifo_java.features.payment;

import com.shifo.shifo_java.features.appointment.Appointment;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import com.shifo.shifo_java.features.payment.model.PaymentType;
import com.shifo.shifo_java.features.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.Instant;

@Entity
@Table(name = "payments",
        indexes = {
                @Index(name = "idx_payments_status", columnList = "status"),
                @Index(name = "idx_payments_paid_at", columnList = "paid_at"),
                @Index(name = "idx_payments_payment_type", columnList = "payment_type"),
                @Index(name = "idx_payments_status_paid_at", columnList = "status, paid_at"),
                @Index(name = "idx_payments_appointment_id", columnList = "appointment_id"),
                @Index(name = "idx_payments_status_type_paid", columnList = "status, payment_type, paid_at"),
                @Index(name = "idx_payments_complex", columnList = "status, paid_at, payment_type, appointment_id")
        }
)
@SQLDelete(sql = "UPDATE payments SET deleted_at = now() WHERE id=?")
@Where(clause = "deleted_at IS NULL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---- Appointment ----
    @Column(name = "appointment_id")
    private Long appointmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", insertable = false, updatable = false)
    private Appointment appointment;

    // ---- Patient ----
    @Column(name = "patient_id")
    private Long patientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
    private Patient patient;

    // ---- User (cashier) ----
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    // ---- Money ----
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    // ---- Enums ----
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_kind", nullable = false)
    private PaymentKind paymentKind = PaymentKind.PAYMENT;

    // ---- Timestamps ----
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}

