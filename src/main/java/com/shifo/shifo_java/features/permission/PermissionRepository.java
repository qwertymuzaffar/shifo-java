package com.shifo.shifo_java.features.permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    boolean existsBySlug(String slug);

    Optional<Permission> findBySlug(String slug);

    List<Permission> findBySlugIn(List<String> slugs);
}
