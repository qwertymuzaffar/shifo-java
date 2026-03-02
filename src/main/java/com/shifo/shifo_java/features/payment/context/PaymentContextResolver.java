package com.shifo.shifo_java.features.payment.context;

import com.shifo.shifo_java.common.exceptions.BadRequestException;
import com.shifo.shifo_java.common.exceptions.NotFoundException;
import com.shifo.shifo_java.features.appointment.Appointment;
import com.shifo.shifo_java.features.appointment.AppointmentRepository;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.patient.PatientRepository;
import com.shifo.shifo_java.features.payment.dto.CreatePaymentDto;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentContextResolver {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    public PaymentContext resolve(CreatePaymentDto dto, PaymentKind kind) {

        Appointment appointment = resolveAppointment(dto, kind);
        Patient patient = resolvePatient(dto, appointment, kind);

        return new PaymentContext(dto, kind, appointment, patient);
    }

    private Appointment resolveAppointment(CreatePaymentDto dto, PaymentKind kind) {

        if (dto.getAppointmentId() == null) {
            if (kind != PaymentKind.PREPAYMENT) {
                throw new BadRequestException("Для данного типа платежа требуется запись на прием");
            }
            return null;
        }

        return appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new NotFoundException("Запись не найдена"));
    }

    private Patient resolvePatient(CreatePaymentDto dto, Appointment appointment, PaymentKind kind) {

        if (appointment != null) {
            return appointment.getPatient();
        }

        if (kind != PaymentKind.PREPAYMENT) {
            throw new BadRequestException("Для данного типа платежа требуется запись на прием");
        }

        if (dto.getPatientId() == null) {
            throw new BadRequestException("Для предоплаты необходимо указать пациента");
        }

        return patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new NotFoundException("Пациент не найден"));
    }
}
