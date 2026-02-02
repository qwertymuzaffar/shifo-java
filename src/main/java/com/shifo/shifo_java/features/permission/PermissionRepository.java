package com.shifo.shifo_java.features.permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Query("""
                SELECT p FROM Permission p
                LEFT JOIN FETCH p.children
                WHERE p.parentId IS NULL
                ORDER BY p.slug
            """)
    List<Permission> findAllRootPermissions();
}
