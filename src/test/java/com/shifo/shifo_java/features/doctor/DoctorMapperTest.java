package com.shifo.shifo_java.features.doctor;

import com.shifo.shifo_java.features.doctor.dto.DoctorDto;
import com.shifo.shifo_java.features.doctor.dto.WorkingHoursDto;
import com.shifo.shifo_java.features.specialization.Specialization;
import com.shifo.shifo_java.features.specialization.SpecializationMapper;
import com.shifo.shifo_java.features.specialization.dto.SpecializationDto;
import com.shifo.shifo_java.features.user.User;
import com.shifo.shifo_java.features.user.UserMapper;
import com.shifo.shifo_java.features.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorMapperTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private SpecializationMapper specializationMapper;

    @InjectMocks
    private DoctorMapper mapper;

    @Test
    void shouldReturnNullForNullDoctor() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void shouldMapAllFieldsIncludingUserAndSpecialization() {
        User user = User.builder()
                .id(3L)
                .firstName("John")
                .lastName("Doe")
                .build();

        Specialization specialization = new Specialization();
        specialization.setId(2L);
        specialization.setName("Cardiology");

        WorkingHoursDto hours = new WorkingHoursDto("09:00", "18:00", List.of(1, 2));

        Doctor doctor = Doctor.builder()
                .id(10L)
                .user(user)
                .isActive(true)
                .experience(8)
                .consultationFee(200)
                .specialization(specialization)
                .workingHours(hours)
                .build();

        UserDto userDto = new UserDto();
        userDto.setId(3L);
        SpecializationDto specializationDto = new SpecializationDto();
        specializationDto.setId(2L);

        when(userMapper.mapUserToDto(user)).thenReturn(userDto);
        when(specializationMapper.toDto(specialization)).thenReturn(specializationDto);

        DoctorDto dto = mapper.toDto(doctor);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getFirstName()).isEqualTo("John");
        assertThat(dto.getLastName()).isEqualTo("Doe");
        assertThat(dto.getIsActive()).isTrue();
        assertThat(dto.getExperience()).isEqualTo(8);
        assertThat(dto.getConsultationFee()).isEqualTo(200);
        assertThat(dto.getWorkingHours()).isSameAs(hours);
        assertThat(dto.getUser()).isSameAs(userDto);
        assertThat(dto.getSpecialization()).isSameAs(specializationDto);
    }

    @Test
    void shouldMapWhenUserIsNull() {
        Doctor doctor = Doctor.builder()
                .id(11L)
                .user(null)
                .isActive(false)
                .build();

        DoctorDto dto = mapper.toDto(doctor);

        assertThat(dto.getId()).isEqualTo(11L);
        assertThat(dto.getFirstName()).isNull();
        assertThat(dto.getLastName()).isNull();
        assertThat(dto.getUser()).isNull();
    }

    @Test
    void shouldMapList() {
        Doctor d1 = Doctor.builder().id(1L).build();
        Doctor d2 = Doctor.builder().id(2L).build();

        List<DoctorDto> dtos = mapper.toDtoList(List.of(d1, d2));

        assertThat(dtos).extracting(DoctorDto::getId).containsExactly(1L, 2L);
    }
}
