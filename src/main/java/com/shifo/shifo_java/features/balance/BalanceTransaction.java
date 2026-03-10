package com.shifo.shifo_java.features.balance;

import com.shifo.shifo_java.features.balance.model.BalanceTransactionType;
import com.shifo.shifo_java.features.balance.model.EntityType;
import com.shifo.shifo_java.features.payment.model.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "balances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Positive = credit, Negative = debit
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    // What caused this record (Payment, future Transaction, etc.)
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 30)
    private EntityType entityType;

    // Accounting meaning of this row
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 40)
    private BalanceTransactionType transactionType;

    // Cash / Card / etc. (informational only)
    @Column(name = "payment_method", length = 50)
    private PaymentType paymentMethod;

    @Column(name = "patient_id")
    private Long patientId;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
