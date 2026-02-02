package com.shifo.shifo_java.features.doctor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    long countByIsActiveTrue();

    long countByIsActiveTrueAndIdIn(List<Long> ids);

    List<Doctor> findByIsActiveTrue();

    List<Doctor> findByIsActiveTrueAndIdIn(List<Long> ids);
}
