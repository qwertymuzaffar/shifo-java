package com.shifo.shifo_java.features.patient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    long countByCreatedAtBetween(Instant start, Instant end);

    Optional<Patient> findByFullName(String defaultName);
}
