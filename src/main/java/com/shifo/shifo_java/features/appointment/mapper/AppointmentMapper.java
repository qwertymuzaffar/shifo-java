package com.shifo.shifo_java.features.appointment.mapper;

import com.shifo.shifo_java.features.appointment.application.command.CreateAppointmentCommand;
import com.shifo.shifo_java.features.appointment.dto.AppointmentDto;
import com.shifo.shifo_java.features.appointment.dto.CreateAppointmentDto;
import com.shifo.shifo_java.features.appointment.dto.DateTimeDto;
import com.shifo.shifo_java.features.appointment.model.Appointment;
import com.shifo.shifo_java.features.appointment.model.AppointmentStatus;
import com.shifo.shifo_java.features.appointment.model.AppointmentType;
import com.shifo.shifo_java.features.doctor.Doctor;
import com.shifo.shifo_java.features.doctor.DoctorMapper;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.patient.PatientMapper;
import com.shifo.shifo_java.features.procedure.Procedure;
import com.shifo.shifo_java.features.procedure.ProcedureMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppointmentMapper {

    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;
    private final ProcedureMapper procedureMapper;

    public AppointmentMapper(DoctorMapper doctorMapper, PatientMapper patientMapper, ProcedureMapper procedureMapper) {
        this.doctorMapper = doctorMapper;
        this.patientMapper = patientMapper;
        this.procedureMapper = procedureMapper;
    }

    public Appointment toEntity(
            CreateAppointmentCommand cmd,
            Doctor doctor,
            Patient patient,
            List<Procedure> procedures
    ) {

        Appointment appointment = new Appointment();

        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDate(cmd.date());
        appointment.setTime(cmd.time());
        appointment.setDuration(cmd.duration());
        appointment.setType(cmd.type());

        appointment.setProcedures(procedures);

        return appointment;
    }

    public AppointmentDto toDto(Appointment entity) {

        if (entity == null) {
            return null;
        }

        AppointmentDto dto = new AppointmentDto();

        dto.setId(entity.getId());
        dto.setDate(entity.getDate());
        dto.setTime(entity.getTime());

        // Relations → expose only IDs (avoid lazy loading serialization issues)
        if (entity.getDoctor() != null) {
            dto.setDoctor(doctorMapper.toDto(entity.getDoctor()));
        }

        if (entity.getPatient() != null) {
            dto.setPatient(patientMapper.toDto(entity.getPatient()));
        }

        dto.setDuration(entity.getDuration());
        dto.setNotes(entity.getNotes());
        dto.setSymptoms(entity.getSymptoms());

        // Enums mapped directly (Jackson will serialize as strings)
        dto.setStatus(entity.getStatus());
        dto.setType(entity.getType());

        dto.setCancellationReason(entity.getCancellationReason());

        // ManyToMany → IDs only
        if (entity.getProcedures() != null && !entity.getProcedures().isEmpty()) {
            dto.setProcedures(
                    entity.getProcedures()
                            .stream()
                            .map(procedureMapper::toDto)
                            .toList()
            );
        }

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

}
