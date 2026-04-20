package com.shifo.shifo_java.features.patient;

import com.shifo.shifo_java.features.patient.dto.CreatePatientDto;
import com.shifo.shifo_java.features.patient.dto.PatientDto;
import com.shifo.shifo_java.features.patient.dto.UpdatePatientDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PatientMapperTest {

    private final PatientMapper mapper = new PatientMapper();

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void shouldMapEntityToDto() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFullName("John Doe");
        patient.setPhone("+992901234567");
        patient.setAddress("Dushanbe");
        patient.setEmergencyContact("Jane");
        patient.setAllergies(List.of("peanuts"));
        patient.setMedicalHistory("asthma");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        patient.setStatus(PatientStatus.ACTIVE);
        patient.setBalance(new BigDecimal("150.00"));
        patient.setCreatedAt(Instant.parse("2026-04-01T00:00:00Z"));
        patient.setUpdatedAt(Instant.parse("2026-04-02T00:00:00Z"));

        PatientDto dto = mapper.toDto(patient);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFullName()).isEqualTo("John Doe");
        assertThat(dto.getPhone()).isEqualTo("+992901234567");
        assertThat(dto.getAddress()).isEqualTo("Dushanbe");
        assertThat(dto.getEmergencyContact()).isEqualTo("Jane");
        assertThat(dto.getAllergies()).containsExactly("peanuts");
        assertThat(dto.getMedicalHistory()).isEqualTo("asthma");
        assertThat(dto.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(dto.getStatus()).isEqualTo(PatientStatus.ACTIVE);
        assertThat(dto.getBalance()).isEqualByComparingTo("150.00");
        assertThat(dto.getCreatedAt()).isEqualTo(Instant.parse("2026-04-01T00:00:00Z"));
        assertThat(dto.getUpdatedAt()).isEqualTo(Instant.parse("2026-04-02T00:00:00Z"));
    }

    @Test
    void shouldMapCreateDtoToEntity() {
        CreatePatientDto dto = new CreatePatientDto();
        dto.setFullName("Jane");
        dto.setPhone("+992900000001");
        dto.setAddress("Khujand");
        dto.setEmergencyContact("Alex");
        dto.setAllergies("Penicillin, PEANUTS , peanuts");
        dto.setMedicalHistory("healthy");
        dto.setBirthDate(LocalDate.of(2000, 2, 3));

        Patient patient = mapper.toEntity(dto);

        assertThat(patient.getFullName()).isEqualTo("Jane");
        assertThat(patient.getPhone()).isEqualTo("+992900000001");
        assertThat(patient.getAddress()).isEqualTo("Khujand");
        assertThat(patient.getEmergencyContact()).isEqualTo("Alex");
        assertThat(patient.getAllergies()).containsExactly("penicillin", "peanuts");
        assertThat(patient.getMedicalHistory()).isEqualTo("healthy");
        assertThat(patient.getBirthDate()).isEqualTo(LocalDate.of(2000, 2, 3));
    }

    @Test
    void shouldReturnEmptyAllergiesForNullOrBlank() {
        CreatePatientDto dto = new CreatePatientDto();
        dto.setAllergies(null);
        assertThat(mapper.toEntity(dto).getAllergies()).isEmpty();

        dto.setAllergies("   ");
        assertThat(mapper.toEntity(dto).getAllergies()).isEmpty();
    }

    @Test
    void shouldMapDtoList() {
        Patient p1 = new Patient();
        p1.setId(1L);
        Patient p2 = new Patient();
        p2.setId(2L);

        List<PatientDto> dtos = mapper.toDtoList(List.of(p1, p2));

        assertThat(dtos).extracting(PatientDto::getId).containsExactly(1L, 2L);
    }

    @Test
    void shouldUpdateOnlyNonNullFields() {
        Patient patient = new Patient();
        patient.setFullName("Old Name");
        patient.setPhone("992900000000");
        patient.setAddress("Old Addr");

        UpdatePatientDto dto = new UpdatePatientDto();
        dto.setFullName("New Name");

        mapper.updateEntity(dto, patient);

        assertThat(patient.getFullName()).isEqualTo("New Name");
        assertThat(patient.getPhone()).isEqualTo("992900000000");
        assertThat(patient.getAddress()).isEqualTo("Old Addr");
    }

    @Test
    void shouldUpdateAllFieldsWhenProvided() {
        Patient patient = new Patient();
        UpdatePatientDto dto = new UpdatePatientDto();
        dto.setFullName("Name");
        dto.setPhone("992900000000");
        dto.setAddress("Addr");
        dto.setEmergencyContact("Contact");
        dto.setMedicalHistory("History");
        dto.setBirthDate(LocalDate.of(1980, 5, 10));
        dto.setAllergies("cats, dogs");

        mapper.updateEntity(dto, patient);

        assertThat(patient.getFullName()).isEqualTo("Name");
        assertThat(patient.getPhone()).isEqualTo("992900000000");
        assertThat(patient.getAddress()).isEqualTo("Addr");
        assertThat(patient.getEmergencyContact()).isEqualTo("Contact");
        assertThat(patient.getMedicalHistory()).isEqualTo("History");
        assertThat(patient.getBirthDate()).isEqualTo(LocalDate.of(1980, 5, 10));
        assertThat(patient.getAllergies()).containsExactly("cats", "dogs");
    }
}
