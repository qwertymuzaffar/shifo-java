package com.shifo.shifo_java.features.doctor.loader;

import com.shifo.shifo_java.common.exceptions.NotFoundException;
import com.shifo.shifo_java.features.role.Role;
import com.shifo.shifo_java.features.role.RoleRepository;
import com.shifo.shifo_java.features.specialization.Specialization;
import com.shifo.shifo_java.features.specialization.SpecializationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorReferenceLoaderTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private SpecializationRepository specializationRepository;

    @InjectMocks
    private DoctorReferenceLoader loader;

    @Test
    void shouldLoadDoctorRoleBySlug() {
        Role role = new Role();
        role.setId(1L);
        role.setSlug("doctor");
        when(roleRepository.findBySlug("doctor")).thenReturn(Optional.of(role));

        assertThat(loader.loadDoctorRole()).isSameAs(role);
    }

    @Test
    void shouldThrowWhenDoctorRoleMissing() {
        when(roleRepository.findBySlug("doctor")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loader.loadDoctorRole())
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("invalidRole");
    }

    @Test
    void shouldLoadSpecializationById() {
        Specialization specialization = new Specialization();
        specialization.setId(5L);
        when(specializationRepository.findById(5L)).thenReturn(Optional.of(specialization));

        assertThat(loader.loadSpecialization(5L)).isSameAs(specialization);
    }

    @Test
    void shouldThrowWhenSpecializationMissing() {
        when(specializationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loader.loadSpecialization(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Specialization not found");
    }
}
