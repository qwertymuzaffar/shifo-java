package com.shifo.shifo_java.features.patient;

import com.shifo.shifo_java.features.patient.dto.CreatePatientDto;
import com.shifo.shifo_java.features.patient.dto.PatientDto;
import com.shifo.shifo_java.common.exceptions.BadRequestException;
import com.shifo.shifo_java.common.exceptions.ResourceNotFoundException;
import com.shifo.shifo_java.features.patient.dto.UpdatePatientDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PatientsService {

    private final PatientRepository patientRepository;

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    @Transactional
    public PatientDto create(CreatePatientDto dto) {
        Patient patient = new Patient();

        patient.setFullName(dto.getFullName());
        patient.setPhone(dto.getPhone());
        patient.setAddress(dto.getAddress());
        patient.setEmergencyContact(dto.getEmergencyContact());
        patient.setAllergies(dto.getAllergies());
        patient.setMedicalHistory(dto.getMedicalHistory());
        patient.setBirthDate(dto.getBirthDate());
        patient.setStatus(1);
        patient.setBalance(BigDecimal.ZERO);

        patientRepository.save(patient);

        return mapToDto(patient);
    }

    // ---------------------------------------------------------
    // FIND ALL WITH FILTERS
    // ---------------------------------------------------------
    public Page<PatientDto> findAll(FilterPatientDto filter) {

        Pageable pageable = PageRequest.of(
                filter.getPage() - 1,
                filter.getLimit(),
                Sort.by("createdAt").descending()
        );

        Specification<Patient> spec = Specification.where(PatientSpec.statusIs(1));

        if (filter.getSearch() != null) {
            spec = spec.and(PatientSpec.search(filter.getSearch()));
        }

        if (filter.getBirthDateFrom() != null) {
            spec = spec.and(PatientSpec.birthDateFrom(filter.getBirthDateFrom()));
        }

        if (filter.getBirthDateTo() != null) {
            spec = spec.and(PatientSpec.birthDateTo(filter.getBirthDateTo()));
        }

        return patientRepository.findAll(spec, pageable)
                .map(this::mapToDto);
    }

    // ---------------------------------------------------------
    // FIND ONE
    // ---------------------------------------------------------
    public PatientDto findOne(Long id) {
        Patient patient = patientRepository.findByIdAndStatus(id, 1)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Пациент с ID " + id + " не найден"));

        return mapToDto(patient);
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    @Transactional
    public PatientDto update(Long id, UpdatePatientDto dto) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Пациент с ID " + id + " не найден"));

        if (dto.getFullName() != null) patient.setFullName(dto.getFullName());
        if (dto.getPhone() != null) patient.setPhone(dto.getPhone());
        if (dto.getAddress() != null) patient.setAddress(dto.getAddress());
        if (dto.getEmergencyContact() != null) patient.setEmergencyContact(dto.getEmergencyContact());
        if (dto.getAllergies() != null) patient.setAllergies(dto.getAllergies());
        if (dto.getMedicalHistory() != null) patient.setMedicalHistory(dto.getMedicalHistory());
        if (dto.getBirthDate() != null) patient.setBirthDate(dto.getBirthDate());

        patientRepository.save(patient);

        return mapToDto(patient);
    }

    // ---------------------------------------------------------
    // REMOVE (Soft Delete)
    // ---------------------------------------------------------
    @Transactional
    public void remove(Long id) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Пациент с ID " + id + " не найден"));

        patient.setStatus(0);  // soft delete

        patientRepository.save(patient);
    }

    // ---------------------------------------------------------
    // UPDATE STATUS
    // ---------------------------------------------------------
    @Transactional
    public PatientDto updateStatus(Long id, Integer status) {

        if (status != 0 && status != 1) {
            throw new BadRequestException("Некорректный статус. Допустимые значения: 0 или 1");
        }

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Пациент с ID " + id + " не найден"));

        patient.setStatus(status);
        patientRepository.save(patient);

        return mapToDto(patient);
    }

    // ---------------------------------------------------------
    // MAP ENTITY → DTO
    // ---------------------------------------------------------
    private PatientDto mapToDto(Patient patient) {
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
}

