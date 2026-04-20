package com.shifo.shifo_java.features.doctor;

import com.shifo.shifo_java.common.exceptions.NotFoundException;
import com.shifo.shifo_java.features.appointment.AppointmentRepository;
import com.shifo.shifo_java.features.appointment.model.AppointmentStatus;
import com.shifo.shifo_java.features.doctor.dto.CreateDoctorDto;
import com.shifo.shifo_java.features.doctor.dto.DoctorDto;
import com.shifo.shifo_java.features.doctor.dto.UpdateDoctorDto;
import com.shifo.shifo_java.features.doctor.dto.WorkingHoursDto;
import com.shifo.shifo_java.features.doctor.factory.DoctorFactory;
import com.shifo.shifo_java.features.doctor.loader.DoctorReferenceLoader;
import com.shifo.shifo_java.features.doctor.validation.DoctorValidator;
import com.shifo.shifo_java.features.role.Role;
import com.shifo.shifo_java.features.specialization.Specialization;
import com.shifo.shifo_java.features.user.User;
import com.shifo.shifo_java.features.user.UserMapper;
import com.shifo.shifo_java.features.user.UserService;
import com.shifo.shifo_java.features.user.dto.UpdateUserDto;
import com.shifo.shifo_java.features.user.factory.UserFactory;
import com.shifo.shifo_java.features.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock private DoctorRepository doctorRepository;
    @Mock private UserRepository userRepository;
    @Mock private AppointmentRepository appointmentRepository;
    @Mock private DoctorMapper doctorMapper;
    @Mock private UserMapper userMapper;
    @Mock private DoctorValidator validator;
    @Mock private DoctorReferenceLoader referenceLoader;
    @Mock private UserFactory userFactory;
    @Mock private DoctorFactory doctorFactory;
    @Mock private UserService usersService;
    @Mock private EntityManager entityManager;

    @InjectMocks
    private DoctorService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "entityManager", entityManager);
    }

    // ---------------- create ----------------

    @Test
    void shouldCreateDoctorUsingFactoriesAndSave() {
        CreateDoctorDto dto = new CreateDoctorDto();
        dto.setSpecializationId(7L);

        Role role = new Role();
        Specialization specialization = new Specialization();
        specialization.setId(7L);
        User user = User.builder().id(3L).build();
        Doctor built = Doctor.builder().build();
        Doctor saved = Doctor.builder().id(44L).build();

        when(referenceLoader.loadDoctorRole()).thenReturn(role);
        when(referenceLoader.loadSpecialization(7L)).thenReturn(specialization);
        when(userFactory.createDoctorUser(dto, role)).thenReturn(user);
        when(doctorFactory.create(user, specialization, dto)).thenReturn(built);
        when(doctorRepository.save(built)).thenReturn(saved);

        Doctor result = service.create(dto);

        assertThat(result).isSameAs(saved);
        verify(validator).validateForCreation(dto);
        verify(userRepository).save(user);
    }

    // ---------------- findOne ----------------

    @Test
    void shouldFindOneAndReturnDto() {
        Doctor doctor = Doctor.builder().id(5L).build();
        DoctorDto dto = new DoctorDto();
        dto.setId(5L);

        when(doctorRepository.findActiveByIdWithRelations(5L)).thenReturn(Optional.of(doctor));
        when(doctorMapper.toDto(doctor)).thenReturn(dto);

        assertThat(service.findOne(5L).getId()).isEqualTo(5L);
    }

    @Test
    void shouldThrowNotFoundWhenFindOneMissing() {
        when(doctorRepository.findActiveByIdWithRelations(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findOne(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("doctors.errors.notFound");
    }

    // ---------------- remove ----------------

    @Test
    void shouldCancelAppointmentsAndRemoveDoctor() {
        User user = User.builder().id(12L).build();
        Doctor doctor = Doctor.builder().id(4L).user(user).build();

        when(doctorRepository.findActiveByIdWithUser(4L)).thenReturn(Optional.of(doctor));

        service.remove(4L);

        verify(appointmentRepository).cancelFutureAppointments(
                4L,
                List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.TEMPORARY),
                AppointmentStatus.CANCELLED_FOREVER
        );
        verify(doctorRepository).softDeactivate(4L);
        verify(usersService).remove(12L);
    }

    @Test
    void shouldThrowNotFoundWhenRemovingMissingDoctor() {
        when(doctorRepository.findActiveByIdWithUser(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.remove(99L))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(appointmentRepository);
    }

    // ---------------- update ----------------

    @Test
    void shouldUpdateUserAndDoctorFields() {
        User user = User.builder().id(8L).build();
        Doctor doctor = Doctor.builder().id(2L).user(user).build();
        Specialization specRef = new Specialization();
        specRef.setId(6L);

        UpdateDoctorDto dto = new UpdateDoctorDto();
        dto.setFirstName("NewName");
        dto.setSpecializationId(6L);
        dto.setExperience(9);
        dto.setConsultationFee(321);
        dto.setWorkingHours(new WorkingHoursDto("08:00", "17:00", List.of(1)));

        UpdateUserDto userDto = new UpdateUserDto();

        when(doctorRepository.findActiveByIdWithUser(2L)).thenReturn(Optional.of(doctor));
        when(userMapper.hasUserChanges(dto)).thenReturn(true);
        when(userMapper.fromDoctorUpdate(dto)).thenReturn(userDto);
        when(entityManager.getReference(Specialization.class, 6L)).thenReturn(specRef);
        when(doctorMapper.toDto(doctor)).thenReturn(new DoctorDto());

        service.update(2L, dto);

        verify(usersService).update(eq(8L), eq(userDto));
        assertThat(doctor.getSpecialization()).isSameAs(specRef);
        assertThat(doctor.getExperience()).isEqualTo(9);
        assertThat(doctor.getConsultationFee()).isEqualTo(321);
        assertThat(doctor.getWorkingHours()).isSameAs(dto.getWorkingHours());
    }

    @Test
    void shouldSkipUserUpdateWhenNoUserChanges() {
        User user = User.builder().id(8L).build();
        Doctor doctor = Doctor.builder().id(2L).user(user).build();

        UpdateDoctorDto dto = new UpdateDoctorDto();

        when(doctorRepository.findActiveByIdWithUser(2L)).thenReturn(Optional.of(doctor));
        when(userMapper.hasUserChanges(dto)).thenReturn(false);
        when(doctorMapper.toDto(doctor)).thenReturn(new DoctorDto());

        service.update(2L, dto);

        verify(usersService, never()).update(any(), any());
        verify(entityManager, never()).getReference(any(), any());
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingMissingDoctor() {
        when(doctorRepository.findActiveByIdWithUser(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, new UpdateDoctorDto()))
                .isInstanceOf(NotFoundException.class);
    }
}
