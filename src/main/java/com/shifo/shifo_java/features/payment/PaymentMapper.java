package com.shifo.shifo_java.features.payment;

import com.shifo.shifo_java.features.patient.PatientMapper;
import com.shifo.shifo_java.features.payment.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentMapper {

    private final PatientMapper patientMapper;

    public PaymentDto toDto(Payment entity) {
        if (entity == null) return null;

        return PaymentDto.builder()
                .id(entity.getId())
                .appointmentId(entity.getAppointmentId())
                .patient(patientMapper.toDto(entity.getPatient()))
                .userId(entity.getUserId())
                .amount(entity.getAmount())
                .paymentType(entity.getPaymentType())
                .status(entity.getStatus())
                .paymentKind(entity.getPaymentKind())
                .paidAt(entity.getPaidAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<PaymentDto> toDtoList(List<Payment> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}
