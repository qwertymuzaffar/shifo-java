package com.shifo.shifo_java.features.patient;

import com.shifo.shifo_java.features.patient.dto.CreatePatientDto;
import com.shifo.shifo_java.features.patient.dto.PatientDto;
import com.shifo.shifo_java.features.patient.dto.UpdatePatientDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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

    public Patient toEntity(CreatePatientDto dto) {
        Patient patient = new Patient();

        patient.setFullName(dto.getFullName());
        patient.setPhone(dto.getPhone());
        patient.setAddress(dto.getAddress());
        patient.setEmergencyContact(dto.getEmergencyContact());
        patient.setAllergies(parseAllergies(dto.getAllergies()));
        patient.setMedicalHistory(dto.getMedicalHistory());
        patient.setBirthDate(dto.getBirthDate());

        return patient;
    }

    public List<PatientDto> toDtoList(List<Patient> patients) {
        return patients.stream()
                .map(this::toDto)
                .toList();
    }

    public void updateEntity(UpdatePatientDto dto, Patient patient) {

        if (dto.getFullName() != null) {
            patient.setFullName(dto.getFullName());
        }

        if (dto.getPhone() != null) {
            patient.setPhone(dto.getPhone());
        }

        if (dto.getAddress() != null) {
            patient.setAddress(dto.getAddress());
        }

        if (dto.getEmergencyContact() != null) {
            patient.setEmergencyContact(dto.getEmergencyContact());
        }

        if (dto.getMedicalHistory() != null) {
            patient.setMedicalHistory(dto.getMedicalHistory());
        }

        if (dto.getBirthDate() != null) {
            patient.setBirthDate(dto.getBirthDate());
        }

        if (dto.getAllergies() != null) {
            patient.setAllergies(parseAllergies(dto.getAllergies()));
        }
    }


    private List<String> parseAllergies(String allergies) {
        if (allergies == null || allergies.isBlank()) {
            return new ArrayList<>();
        }

        return Arrays.stream(allergies.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)   // optional normalization
                .distinct()
                .toList();
    }
}
