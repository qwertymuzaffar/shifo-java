package com.shifo.shifo_java.features.doctor;

import com.shifo.shifo_java.common.exceptions.BadRequestException;
import com.shifo.shifo_java.features.doctor.dto.DoctorDto;
import com.shifo.shifo_java.features.role.Role;
import com.shifo.shifo_java.features.specialization.Specialization;
import com.shifo.shifo_java.features.user.User;
import com.shifo.shifo_java.common.exceptions.ResourceNotFoundException;
import com.shifo.shifo_java.features.specialization.SpecializationRepository;
import com.shifo.shifo_java.service.RbacService;
import com.shifo.shifo_java.features.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserService userService;
    private final RbacService rbacService;
    private final SpecializationRepository specializationRepository;

    // -----------------------------
    // CREATE DOCTOR
    // -----------------------------
    @Transactional
    public DoctorDto createDoctor(DoctorDto dto) {

        // 1. Check email
        if (userService.existsByEmail(dto.getEmail())) {
            throw new ResourceNotFoundException("Email already exists");
        }

        // 2. Find doctor role
        Role doctorRole = rbacService.findBySlug("doctor")
                .orElseThrow(() -> new ResourceNotFoundException("Doctor role not found"));

        // 3. Create user
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setRoleId(doctorRole.getId());
        userService.create(user, dto.getPassword());

        // 4. Create doctor entity
        Doctor doctor = new Doctor();
        doctor.setUserId(user.getId());
        doctor.setIsActive(dto.getIsActive());
        doctor.setStatus(1);
        doctor.setExperience(dto.getExperience());
        doctor.setConsultationFee(dto.getConsultationFee());
        doctor.setWorkingHours(dto.getWorkingHours());

        // set specialization
        if (dto.getSpecializationId() != null) {
            Specialization specialization = specializationRepository.findById(dto.getSpecializationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialization not found"));
            doctor.setSpecialization(specialization);
        }

        doctorRepository.save(doctor);

        return mapToDto(doctor);
    }

    // -----------------------------
    // FIND ALL WITH FILTERS
    // -----------------------------
    public Page<DoctorDto> findAll(DoctorFilterDto filterDto) {

        Pageable pageable = PageRequest.of(
                filterDto.getPage() - 1,
                filterDto.getLimit(),
                Sort.by("createdAt").descending()
        );

        Specification<Doctor> spec = Specification.where(DoctorSpec.statusIs(1));

        if (filterDto.getSearch() != null) {
            spec = spec.and(DoctorSpec.search(filterDto.getSearch()));
        }

        if (filterDto.getIsActive() != null) {
            spec = spec.and(DoctorSpec.isActive(filterDto.getIsActive()));
        }

        if (filterDto.getSpecializationId() != null) {
            spec = spec.and(DoctorSpec.specializationId(filterDto.getSpecializationId()));
        }

        if (filterDto.getRoomId() != null) {
            spec = spec.and(DoctorSpec.roomId(filterDto.getRoomId()));
        }

        return doctorRepository.findAll(spec, pageable)
                .map(this::mapToDto);
    }

    // -----------------------------
    // FIND ONE
    // -----------------------------
    public DoctorDto findOne(Long id) {
        Doctor doctor = doctorRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        return mapToDto(doctor);
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @Transactional
    public DoctorDto update(Long id, DoctorDto dto) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        // update user info
        if (dto.getUsername() != null ||
                dto.getEmail() != null ||
                dto.getFirstName() != null) {
            userService.update(doctor.getUserId(), dto);
        }

        // update specialization
        if (dto.getSpecializationId() != null) {
            Specialization specialization = specializationRepository
                    .findById(dto.getSpecializationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialization not found"));
            doctor.setSpecialization(specialization);
        }

        doctor.setExperience(dto.getExperience());
        doctor.setConsultationFee(dto.getConsultationFee());
        doctor.setWorkingHours(dto.getWorkingHours());

        doctorRepository.save(doctor);

        return mapToDto(doctor);
    }

    // -----------------------------
    // REMOVE / SOFT DELETE
    // -----------------------------
    @Transactional
    public void remove(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        doctor.setStatus(0); // soft delete
        doctorRepository.save(doctor);

        userService.remove(doctor.getUserId());
    }

    // -----------------------------
    // UPDATE STATUS
    // -----------------------------
    @Transactional
    public DoctorDto updateStatus(Long id, Integer status) {
        if (status != 0 && status != 1) {
            throw new BadRequestException("Status must be 0 or 1");
        }

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        doctor.setStatus(status);
        doctorRepository.save(doctor);

        return mapToDto(doctor);
    }

    // -----------------------------
    // MAP ENTITY TO DTO
    // -----------------------------
    private DoctorDto mapToDto(Doctor doctor) {
        DoctorDto dto = new DoctorDto();

        dto.setId(doctor.getId());
        dto.setUserId(doctor.getUserId());
        dto.setFullName(doctor.getFullName());
        dto.setFirstName(doctor.getFirstName());
        dto.setLastName(doctor.getLastName());
        dto.setSpecializationId(doctor.getSpecialization() != null
                ? doctor.getSpecialization().getId() : null);
        dto.setExperience(doctor.getExperience());
        dto.setConsultationFee(doctor.getConsultationFee());
        dto.setWorkingHours(doctor.getWorkingHours());
        dto.setIsActive(doctor.getIsActive());
        dto.setStatus(doctor.getStatus());
        dto.setCreatedAt(doctor.getCreatedAt());
        dto.setUpdatedAt(doctor.getUpdatedAt());

        return dto;
    }
}

