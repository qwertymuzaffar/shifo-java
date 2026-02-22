package com.shifo.shifo_java.features.appointment;

import com.shifo.shifo_java.features.appointment.dto.AppointmentDto;
import com.shifo.shifo_java.features.appointment.dto.CreateAppointmentDto;
import com.shifo.shifo_java.features.appointment.dto.DateTimeDto;
import com.shifo.shifo_java.features.appointment.model.Appointment;
import com.shifo.shifo_java.features.appointment.model.AppointmentStatus;
import com.shifo.shifo_java.features.appointment.model.AppointmentType;
import com.shifo.shifo_java.features.doctor.Doctor;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.procedure.Procedure;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppointmentMapper {

    public Appointment toEntity(
            CreateAppointmentDto dto,
            DateTimeDto datetime,
            Doctor doctor,
            Patient patient,
            List<Procedure> procedures
    ) {
        Appointment appointment = new Appointment();

        appointment.setDoctor(doctor);
        appointment.setPatient(patient);

        appointment.setDate(datetime.getDate());
        appointment.setTime(datetime.getTime());

        appointment.setDuration(dto.getDuration());
        appointment.setNotes(dto.getNotes());
        appointment.setSymptoms(dto.getSymptoms());

        appointment.setProcedures(procedures);

        appointment.setType(
                dto.getType() != null ? dto.getType() : AppointmentType.CONSULTATION
        );

        appointment.setStatus(
                dto.getStatus() != null ? dto.getStatus() : AppointmentStatus.SCHEDULED
        );

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
            dto.setDoctorId(entity.getDoctor().getId());
        }

        if (entity.getPatient() != null) {
            dto.setPatientId(entity.getPatient().getId());
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
            dto.setProcedureIds(
                    entity.getProcedures()
                            .stream()
                            .map(Procedure::getId)
                            .toList()
            );
        }

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

}
