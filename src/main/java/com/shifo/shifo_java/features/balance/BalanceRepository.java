package com.shifo.shifo_java.features.balance;

import com.shifo.shifo_java.features.balance.model.BalanceTransaction;
import com.shifo.shifo_java.features.balance.model.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceRepository extends JpaRepository<BalanceTransaction, Long> {
    boolean existsByEntityTypeAndEntityId(EntityType entityType, Long entityId);
}
