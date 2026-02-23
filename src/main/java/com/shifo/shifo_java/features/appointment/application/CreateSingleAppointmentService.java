package com.shifo.shifo_java.features.appointment.application;

import com.shifo.shifo_java.features.appointment.AppointmentRepository;
import com.shifo.shifo_java.features.appointment.application.command.CreateAppointmentCommand;
import com.shifo.shifo_java.features.appointment.domain.AppointmentAvailabilityChecker;
import com.shifo.shifo_java.features.appointment.dto.AppointmentDto;
import com.shifo.shifo_java.features.appointment.mapper.AppointmentMapper;
import com.shifo.shifo_java.features.appointment.Appointment;
import com.shifo.shifo_java.features.doctor.Doctor;
import com.shifo.shifo_java.features.doctor.DoctorRepository;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.patient.PatientProvider;
import com.shifo.shifo_java.features.procedure.Procedure;
import com.shifo.shifo_java.features.procedure.ProcedureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateSingleAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final ProcedureRepository procedureRepository;
    private final AppointmentMapper appointmentMapper;
    private final PatientProvider patientProvider;
    private final AppointmentAvailabilityChecker availabilityChecker;

    @Transactional
    public AppointmentDto create(CreateAppointmentCommand cmd) {

        Doctor doctor = doctorRepository.findById(cmd.doctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Patient patient = patientProvider.resolve(cmd.patientId());

        List<Procedure> procedures = loadProcedures(cmd.procedureIds());

        availabilityChecker.ensureAvailable(
                doctor.getId(),
                patient.getId(),
                cmd.date(),
                cmd.time(),
                cmd.duration()
        );

        Appointment appointment =
                appointmentMapper.toEntity(cmd, doctor, patient, procedures);

        appointmentRepository.save(appointment);

        return appointmentMapper.toDto(appointment);
    }

    private List<Procedure> loadProcedures(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();

        List<Procedure> procedures = procedureRepository.findAllById(ids);

        if (procedures.size() != ids.size()) {
            throw new IllegalArgumentException("Invalid procedure IDs");
        }

        return procedures;
    }
}
