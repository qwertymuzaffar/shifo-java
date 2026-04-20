package com.shifo.shifo_java.features.patient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientProviderTest {

    @Mock private PatientRepository patientRepository;

    @InjectMocks private PatientProvider provider;

    @Test
    void shouldResolveByIdWhenProvided() {
        Patient patient = new Patient();
        patient.setId(7L);

        when(patientRepository.findById(7L)).thenReturn(Optional.of(patient));

        Patient resolved = provider.resolve(7L);

        assertThat(resolved).isSameAs(patient);
    }

    @Test
    void shouldThrowWhenPatientIdNotFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> provider.resolve(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void shouldReturnExistingDefaultPatientWhenIdIsNull() {
        Patient defaultPatient = new Patient();
        defaultPatient.setId(1L);
        defaultPatient.setFullName("Default Patient");

        when(patientRepository.findByFullName("Default Patient"))
                .thenReturn(Optional.of(defaultPatient));

        Patient resolved = provider.resolve(null);

        assertThat(resolved).isSameAs(defaultPatient);
        verify(patientRepository, never()).save(defaultPatient);
    }

    @Test
    void shouldCreateDefaultPatientWhenMissing() {
        when(patientRepository.findByFullName("Default Patient"))
                .thenReturn(Optional.empty());
        when(patientRepository.save(org.mockito.ArgumentMatchers.any(Patient.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Patient resolved = provider.resolve(null);

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(captor.capture());

        assertThat(captor.getValue().getFullName()).isEqualTo("Default Patient");
        assertThat(captor.getValue().getPhone()).isEqualTo("000000000");
        assertThat(captor.getValue().getBirthDate()).isNotNull();
        assertThat(resolved).isSameAs(captor.getValue());
    }
}
