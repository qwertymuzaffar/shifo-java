package com.shifo.shifo_java.features.user.repository;

import com.shifo.shifo_java.features.user.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>, UserRepositoryCustom {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Modifying
    @Query("""
                UPDATE User u
                SET u.deletedAt = CURRENT_TIMESTAMP
                WHERE u.id = :id AND u.deletedAt IS NULL
            """)
    int softDelete(@Param("id") Long id);

    @Query("""
            SELECT u
            FROM User u
            LEFT JOIN FETCH u.role
            WHERE u.id = :id
            """)
    Optional<User> findByIdWithRole(@Param("id") Long id);

}
