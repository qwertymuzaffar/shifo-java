package com.shifo.shifo_java.features.balance;

import com.shifo.shifo_java.features.balance.model.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
    boolean existsByEntityTypeAndEntityId(EntityType entityType, Long entityId);

    Optional<Balance> findByEntityIdAndEntityType(
            Long entityId,
            EntityType entityType
    );
}
