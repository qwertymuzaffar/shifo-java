package com.shifo.shifo_java.features.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findBySlug(String slug);
    Optional<Role> findByName(String name);

    @Query("""
       SELECT r FROM Role r
       LEFT JOIN FETCH r.permissions
       WHERE r.id = :id
       """)
    Optional<Role> findByIdWithPermissions(Long id);

    boolean existsByName(String name);
}
