package com.shifo.shifo_java.features.patient;

import com.shifo.shifo_java.features.patient.dto.PatientDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PatientMapper {

    public PatientDto toDto(Patient patient) {
        if (patient == null) {
            return null;
        }

        PatientDto dto = new PatientDto();
        dto.setId(patient.getId());
        dto.setFullName(patient.getFullName());
        dto.setPhone(patient.getPhone());
        dto.setAddress(patient.getAddress());
        dto.setEmergencyContact(patient.getEmergencyContact());
        dto.setAllergies(patient.getAllergies());
        dto.setMedicalHistory(patient.getMedicalHistory());
        dto.setBirthDate(patient.getBirthDate());
        dto.setStatus(patient.getStatus());
        dto.setBalance(patient.getBalance());
        dto.setCreatedAt(patient.getCreatedAt());
        dto.setUpdatedAt(patient.getUpdatedAt());
        return dto;
    }

    public List<PatientDto> toDtoList(List<Patient> patients) {
        return patients.stream()
                .map(this::toDto)
                .toList();
    }
}
