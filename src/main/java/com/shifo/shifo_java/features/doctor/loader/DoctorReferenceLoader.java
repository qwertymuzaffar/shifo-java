package com.shifo.shifo_java.features.doctor.loader;

import com.shifo.shifo_java.common.exceptions.NotFoundException;
import com.shifo.shifo_java.features.role.Role;
import com.shifo.shifo_java.features.role.RoleRepository;
import com.shifo.shifo_java.features.specialization.Specialization;
import com.shifo.shifo_java.features.specialization.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DoctorReferenceLoader {

    private final RoleRepository roleRepository;
    private final SpecializationRepository specializationRepository;

    public Role loadDoctorRole() {
        return roleRepository.findBySlug("doctor")
                .orElseThrow(() -> new NotFoundException("users.errors.invalidRole"));
    }

    public Specialization loadSpecialization(Long id) {
        return specializationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Specialization not found"));
    }
}

