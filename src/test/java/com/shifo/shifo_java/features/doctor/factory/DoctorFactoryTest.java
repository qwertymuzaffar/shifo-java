package com.shifo.shifo_java.features.doctor.factory;

import com.shifo.shifo_java.features.doctor.Doctor;
import com.shifo.shifo_java.features.doctor.dto.CreateDoctorDto;
import com.shifo.shifo_java.features.doctor.dto.WorkingHoursDto;
import com.shifo.shifo_java.features.specialization.Specialization;
import com.shifo.shifo_java.features.user.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DoctorFactoryTest {

    private final DoctorFactory factory = new DoctorFactory();

    @Test
    void shouldBuildDoctorFromContext() {
        User user = User.builder().id(7L).build();
        Specialization specialization = new Specialization();
        specialization.setId(12L);

        WorkingHoursDto hours = new WorkingHoursDto("09:00", "18:00", List.of(1, 2, 3));

        CreateDoctorDto dto = new CreateDoctorDto();
        dto.setExperience(5);
        dto.setConsultationFee(250);
        dto.setWorkingHours(hours);

        Doctor doctor = factory.create(user, specialization, dto);

        assertThat(doctor.getUser()).isSameAs(user);
        assertThat(doctor.getSpecialization()).isSameAs(specialization);
        assertThat(doctor.getExperience()).isEqualTo(5);
        assertThat(doctor.getConsultationFee()).isEqualTo(250);
        assertThat(doctor.getWorkingHours()).isSameAs(hours);
    }

    @Test
    void shouldBuildDoctorWithNullOptionalFields() {
        CreateDoctorDto dto = new CreateDoctorDto();
        dto.setExperience(null);
        dto.setConsultationFee(null);
        dto.setWorkingHours(null);

        Doctor doctor = factory.create(null, null, dto);

        assertThat(doctor.getUser()).isNull();
        assertThat(doctor.getSpecialization()).isNull();
        assertThat(doctor.getExperience()).isNull();
        assertThat(doctor.getConsultationFee()).isNull();
        assertThat(doctor.getWorkingHours()).isNull();
    }
}
