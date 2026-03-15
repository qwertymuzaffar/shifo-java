package com.shifo.shifo_java.features.balance;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.shifo.shifo_java.features.balance.model.EntityType;
import com.shifo.shifo_java.features.finance.transaction.Transaction;
import com.shifo.shifo_java.features.payment.Payment;
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
public class Balance {

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

    @Column(name = "payment_method", length = 50)
    private PaymentType paymentMethod;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Transient
    private Payment payment;

    @Transient
    private Transaction transaction;

    /**
     * Getter for related entity
     * @return
     */
    @JsonGetter("relatedEntity")
    public Object getRelatedEntity() {
        if (entityType == EntityType.PAYMENT) {
            return payment;
        }

        if (entityType == EntityType.TRANSACTION) {
            return transaction;
        }

        return null;
    }

    /**
     * Getter for entity type name
     * @return
     */
    @JsonGetter("entityTypeName")
    public String getEntityTypeName() {
        if (entityType == EntityType.PAYMENT) {
            return "payment";
        }
        return "transaction";
    }
}
