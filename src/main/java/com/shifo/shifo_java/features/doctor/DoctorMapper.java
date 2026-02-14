package com.shifo.shifo_java.features.doctor;

import com.shifo.shifo_java.features.doctor.dto.DoctorDto;
import com.shifo.shifo_java.features.specialization.SpecializationMapper;
import com.shifo.shifo_java.features.user.UserMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DoctorMapper {

    private final UserMapper userMapper;
    private final SpecializationMapper specializationMapper;
    public DoctorMapper(UserMapper userMapper, SpecializationMapper specializationMapper) {
        this.userMapper = userMapper;
        this.specializationMapper = specializationMapper;
    }

    public DoctorDto toDto(Doctor doctor) {

        if (doctor == null) {
            return null;
        }

        DoctorDto dto = new DoctorDto();
        dto.setId(doctor.getId());
        dto.setFirstName(doctor.getUser() != null ? doctor.getUser().getFirstName() : null);
        dto.setLastName(doctor.getUser() != null ? doctor.getUser().getLastName() : null);
        dto.setIsActive(doctor.getIsActive());
        dto.setExperience(doctor.getExperience());
        dto.setWorkingHours(doctor.getWorkingHours());
        dto.setUser(userMapper.mapUserToDto(doctor.getUser()));
        dto.setConsultationFee(doctor.getConsultationFee());
        dto.setSpecialization(specializationMapper.toDto(doctor.getSpecialization()));
        return dto;
    }

    public List<DoctorDto> toDtoList(List<Doctor> doctors) {
        return doctors.stream()
                .map(this::toDto)
                .toList();
    }
}
