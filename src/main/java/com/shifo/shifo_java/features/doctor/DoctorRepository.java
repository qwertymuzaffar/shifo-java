package com.shifo.shifo_java.features.doctor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    long countByIsActiveTrue();

    long countByIsActiveTrueAndIdIn(List<Long> ids);

    List<Doctor> findByIsActiveTrue();

    List<Doctor> findByIsActiveTrueAndIdIn(List<Long> ids);

    @Query("""
                SELECT d FROM Doctor d
                LEFT JOIN FETCH d.user
                LEFT JOIN FETCH d.specialization
                WHERE d.id = :id AND d.status = 1
            """)
    Optional<Doctor> findActiveByIdWithRelations(@Param("id") Long id);

    @Modifying
    @Query("""
                UPDATE Doctor d
                SET d.status = 0
                WHERE d.id = :id
            """)
    int softDeactivate(@Param("id") Long id);

    @Query("""
                SELECT d FROM Doctor d
                LEFT JOIN FETCH d.user
                WHERE d.id = :id AND d.status = 1
            """)
    Optional<Doctor> findActiveByIdWithUser(@Param("id") Long id);

    @Query(value = """
                SELECT *
                FROM doctors d
                WHERE d.id = :id
            """, nativeQuery = true)
    Doctor findByIdIncludingDeleted(Long id);

}
