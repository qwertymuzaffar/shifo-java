package com.shifo.shifo_java.features.patient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class PatientProvider {

    private final PatientRepository patientRepository;

    public Patient resolve(Long patientId) {

        if (patientId != null) {
            return patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("Patient not found"));
        }

        String defaultName = "Default Patient";

        return patientRepository.findByFullName(defaultName)
                .orElseGet(() -> {
                    Patient p = new Patient();
                    p.setFullName(defaultName);
                    p.setPhone("000000000");
                    p.setBirthDate(LocalDate.now());
                    return patientRepository.save(p);
                });
    }
}
