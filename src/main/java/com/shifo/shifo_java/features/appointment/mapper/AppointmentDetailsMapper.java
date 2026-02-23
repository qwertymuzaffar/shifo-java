package com.shifo.shifo_java.features.appointment.mapper;

import com.shifo.shifo_java.features.appointment.dto.AppointmentDetailsDto;
import com.shifo.shifo_java.features.appointment.Appointment;
import com.shifo.shifo_java.features.doctor.DoctorMapper;
import com.shifo.shifo_java.features.patient.PatientMapper;
import com.shifo.shifo_java.features.procedure.ProcedureMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentDetailsMapper {

    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;
    private final ProcedureMapper procedureMapper;
//    private final PaymentMapper paymentMapper;

    public AppointmentDetailsDto toDto(Appointment a) {

        AppointmentDetailsDto dto = new AppointmentDetailsDto();

        dto.setId(a.getId());
        dto.setDate(a.getDate());
        dto.setTime(a.getTime());
        dto.setDuration(a.getDuration());
        dto.setStatus(a.getStatus());
        dto.setType(a.getType());

        dto.setNotes(a.getNotes());
        dto.setSymptoms(a.getSymptoms());
        dto.setCancellationReason(a.getCancellationReason());

        dto.setDoctor(doctorMapper.toDto(a.getDoctor()));
        dto.setPatient(patientMapper.toDto(a.getPatient()));
        dto.setProcedures(procedureMapper.toDtoList(a.getProcedures()));
//        dto.setPayments(paymentMapper.toDtoList(a.getPayments()));

        dto.setCreatedAt(a.getCreatedAt());
        dto.setUpdatedAt(a.getUpdatedAt());

        return dto;
    }
}
