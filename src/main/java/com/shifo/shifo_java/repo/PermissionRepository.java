package com.shifo.shifo_java.repo;

import com.shifo.shifo_java.features.permission.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
