package com.shifo.shifo_java.features.patient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    long countByCreatedAtBetween(Instant start, Instant end);
}
