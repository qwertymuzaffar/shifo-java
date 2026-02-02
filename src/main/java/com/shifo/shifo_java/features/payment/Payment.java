package com.shifo.shifo_java.features.payment;

import com.shifo.shifo_java.common.enums.PaymentStatus;
import com.shifo.shifo_java.common.enums.PaymentType;
import com.shifo.shifo_java.features.appointment.Appointment;
import com.shifo.shifo_java.common.enums.PaymentKind;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Raw FK column for convenience (like in NestJS)
    @Column(name = "appointment_id", nullable = false)
    private Long appointmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", insertable = false, updatable = false)
    private Appointment appointment;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    // -------- ENUMS --------
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;   // nullable

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_kind", nullable = false)
    private PaymentKind paymentKind = PaymentKind.PAYMENT;

    // -------- TIMESTAMPS --------
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}

