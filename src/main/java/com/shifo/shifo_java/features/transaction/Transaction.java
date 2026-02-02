package com.shifo.shifo_java.features.transaction;

import com.shifo.shifo_java.common.enums.PaymentMethod;
import com.shifo.shifo_java.common.enums.RelatedEntityType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Тип операции: приход / расход
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    // Тип оплаты
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    // Сумма транзакции
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    // Комментарий
    @Column(columnDefinition = "TEXT")
    private String comment;

    // ID врача / пациента
    @Column(name = "related_entity_id")
    private Long relatedEntityId;

    // Тип связанной сущности
    @Enumerated(EnumType.STRING)
    @Column(name = "related_entity_type")
    private RelatedEntityType relatedEntityType;

    // Virtual fields in NestJS → only @Transient in Java
    @Transient
    private Object patient;

    @Transient
    private Object doctor;

    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}

