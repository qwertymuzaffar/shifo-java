package com.shifo.shifo_java.features.doctor.factory;

import com.shifo.shifo_java.features.doctor.Doctor;
import com.shifo.shifo_java.features.doctor.dto.CreateDoctorDto;
import com.shifo.shifo_java.features.specialization.Specialization;
import com.shifo.shifo_java.features.user.User;
import org.springframework.stereotype.Component;

@Component
public class DoctorFactory {

    public Doctor create(User user,
                         Specialization specialization,
                         CreateDoctorDto dto) {

        return Doctor.builder()
                .user(user)
                .specialization(specialization)
                .workingHours(dto.getWorkingHours())
                .experience(dto.getExperience())
                .consultationFee(dto.getConsultationFee())
                .build();
    }
}

