package com.shifo.shifo_java.features.patient;

import com.shifo.shifo_java.common.exceptions.NotFoundException;
import com.shifo.shifo_java.features.patient.dto.CreatePatientDto;
import com.shifo.shifo_java.features.patient.dto.PatientDto;
import com.shifo.shifo_java.features.patient.dto.UpdatePatientDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientsServiceTest {

    @Mock private PatientRepository patientRepository;
    @Mock private EntityManager entityManager;
    @Mock private PatientMapper patientMapper;

    @InjectMocks private PatientsService service;

    @Test
    void shouldCreatePatientViaMapperAndRepository() {
        CreatePatientDto dto = new CreatePatientDto();
        Patient mapped = new Patient();
        Patient saved = new Patient();
        saved.setId(10L);

        when(patientMapper.toEntity(dto)).thenReturn(mapped);
        when(patientRepository.save(mapped)).thenReturn(saved);

        Patient result = service.create(dto);

        assertThat(result).isSameAs(saved);
    }

    @Test
    void shouldFindOneAndReturnDto() {
        Patient patient = new Patient();
        patient.setId(1L);
        PatientDto dto = new PatientDto();
        dto.setId(1L);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientMapper.toDto(patient)).thenReturn(dto);

        PatientDto result = service.findOne(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowNotFoundWhenPatientMissingOnFindOne() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findOne(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("id=99");
    }

    @Test
    void shouldUpdatePatientViaMapper() {
        Patient patient = new Patient();
        patient.setId(2L);
        UpdatePatientDto dto = new UpdatePatientDto();
        PatientDto returnedDto = new PatientDto();
        returnedDto.setId(2L);

        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(patientMapper.toDto(patient)).thenReturn(returnedDto);

        PatientDto result = service.update(2L, dto);

        assertThat(result.getId()).isEqualTo(2L);
        verify(patientMapper).updateEntity(dto, patient);
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingMissingPatient() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, new UpdatePatientDto()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldSetInactiveStatusOnDeactivate() {
        Patient patient = new Patient();
        patient.setId(3L);
        patient.setStatus(PatientStatus.ACTIVE);

        when(patientRepository.findById(3L)).thenReturn(Optional.of(patient));

        service.deactivate(3L);

        assertThat(patient.getStatus()).isEqualTo(PatientStatus.INACTIVE);
        verify(patientRepository).save(patient);
    }

    @Test
    void shouldThrowNotFoundWhenDeactivatingMissingPatient() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deactivate(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowWhenUpdateStatusReceivesNull() {
        assertThatThrownBy(() -> service.updateStatus(1L, null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(patientRepository, never()).findById(any());
    }

    @Test
    void shouldUpdatePatientStatus() {
        Patient patient = new Patient();
        patient.setId(4L);
        patient.setStatus(PatientStatus.ACTIVE);
        PatientDto dto = new PatientDto();

        when(patientRepository.findById(4L)).thenReturn(Optional.of(patient));
        when(patientMapper.toDto(patient)).thenReturn(dto);

        service.updateStatus(4L, PatientStatus.INACTIVE);

        assertThat(patient.getStatus()).isEqualTo(PatientStatus.INACTIVE);
        verify(patientRepository).save(patient);
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingStatusOfMissingPatient() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus(99L, PatientStatus.ACTIVE))
                .isInstanceOf(NotFoundException.class);
    }
}
