package com.shifo.shifo_java.features.finance.category;

import com.shifo.shifo_java.features.finance.transaction.Transaction;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.Instant;
import java.util.List;

@Entity
@Table(
        name = "transaction_categories",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "deleted_at"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE transaction_categories SET deleted_at = NOW() WHERE id=?")
@Where(clause = "deleted_at IS NULL")
public class TransactionCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Integer sortOrder = 0;

    @Column(updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    private Instant deletedAt;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
