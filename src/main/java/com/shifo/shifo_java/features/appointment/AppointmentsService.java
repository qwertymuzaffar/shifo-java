package com.shifo.shifo_java.features.appointment;

import com.shifo.shifo_java.features.appointment.dto.*;
import com.shifo.shifo_java.features.appointment.model.Appointment;
import com.shifo.shifo_java.features.appointment.specification.AppointmentSpecification;
import com.shifo.shifo_java.features.doctor.Doctor;
import com.shifo.shifo_java.features.doctor.DoctorRepository;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.patient.PatientRepository;
import com.shifo.shifo_java.features.procedure.Procedure;
import com.shifo.shifo_java.features.procedure.ProcedureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentsService {


    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ProcedureRepository procedureRepository;
    private final AppointmentMapper appointmentMapper;
    private final TransactionTemplate transactionTemplate;

    /**
     * Equivalent of NestJS create() — batch creation with partial success.
     */
    public CreateAppointmentResultDto create(CreateAppointmentDto dto) {

        Doctor doctor = validateDoctor(dto.getDoctorId());
        Patient patient = resolvePatient(dto.getPatientId());
        List<Procedure> procedures = fetchProcedures(dto.getProcedureIds());

        List<AppointmentDto> successful = new ArrayList<>();
        List<FailedAppointmentDto> failed = new ArrayList<>();

        for (DateTimeDto datetime : dto.getDatetimes()) {

            try {
                AppointmentDto created = transactionTemplate.execute(status -> {

                    // Availability validation (doctor always checked)
                    checkAvailabilityForDoctor(
                            doctor.getId(),
                            datetime.getDate(),
                            datetime.getTime(),
                            dto.getDuration()
                    );

                    // Patient availability checked only if real patient provided
                    if (dto.getPatientId() != null) {
                        checkAvailabilityForPatient(
                                patient.getId(),
                                datetime.getDate(),
                                datetime.getTime(),
                                dto.getDuration()
                        );
                    }

                    // Create entity via mapper (clean separation)
                    Appointment appointment =
                            appointmentMapper.toEntity(dto, datetime, doctor, patient, procedures);

                    appointmentRepository.save(appointment);

                    return appointmentMapper.toDto(appointment);
                });

                successful.add(created);

            } catch (Exception ex) {
                failed.add(new FailedAppointmentDto(
                        datetime.getDate(),
                        datetime.getTime(),
                        ex.getMessage()
                ));
            }
        }

        return new CreateAppointmentResultDto(successful, failed);
    }

    private Doctor validateDoctor(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + doctorId));
    }

    private Patient resolvePatient(Long patientId) {

        if (patientId != null) {
            return patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("Patient not found: " + patientId));
        }

        String defaultName = "Default Patient";

        return patientRepository.findByFullName(defaultName)
                .orElseGet(() -> {
                    Patient p = new Patient();
                    p.setFullName(defaultName);
                    p.setPhone("000000000");
                    p.setBirthDate(LocalDate.now());
                    return patientRepository.save(p);
                });
    }

    private List<Procedure> fetchProcedures(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<Procedure> procedures = procedureRepository.findAllById(ids);

        if (procedures.size() != ids.size()) {
            throw new IllegalArgumentException("Invalid procedure IDs");
        }

        return procedures;
    }

    private void checkAvailabilityForDoctor(
            Long doctorId,
            LocalDate date,
            LocalTime time,
            Integer duration
    ) {
        LocalTime endTime = time.plusMinutes(duration);

        boolean exists = appointmentRepository.existsDoctorOverlap(
                doctorId,
                date,
                time,
                endTime
        ) == 1;

        if (exists) {
            throw new IllegalStateException("Doctor already has an appointment in this time range");
        }
    }

    private void checkAvailabilityForPatient(
            Long patientId,
            LocalDate date,
            LocalTime time,
            Integer duration
    ) {
        LocalTime endTime = time.plusMinutes(duration);

        boolean exists = appointmentRepository.existsPatientOverlap(
                patientId,
                date,
                time,
                endTime
        ) == 1;

        if (exists) {
            throw new IllegalStateException("Patient already has an appointment in this time range");
        }
    }

    public List<AppointmentDto> findAll(FilterAppointmentDto filter) {

        Specification<Appointment> specification = AppointmentSpecification.build(filter);
        Pageable pageable = PageRequest.of(filter.getPage() - 1, filter.getLimit());
        Page<Appointment> page = appointmentRepository.findAll(specification, pageable);
        List<Appointment> entities = page.getContent();

        return entities.stream()
                .map(appointmentMapper::toDto)
                .toList();
    }
}
