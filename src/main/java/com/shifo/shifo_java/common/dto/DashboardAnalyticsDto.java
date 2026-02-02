package com.shifo.shifo_java.common.dto;

import com.shifo.shifo_java.features.appointment.dto.AppointmentStatusCountDto;
import com.shifo.shifo_java.features.appointment.dto.AppointmentTypeCountDto;
import com.shifo.shifo_java.features.appointment.dto.TotalAppointmentsDto;
import com.shifo.shifo_java.features.doctor.dto.DoctorRatingDto;
import com.shifo.shifo_java.features.patient.dto.TotalPatientsDto;
import com.shifo.shifo_java.features.payment.dto.PaymentMethodStatsDto;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DashboardAnalyticsDto {
    private TotalPatientsDto totalPatients;
    private long totalActiveDoctors;
    private TotalAppointmentsDto totalAppointments;
    private TotalRevenueDto totalRevenue;
    private AppointmentStatusCountDto appointmentStatuses;
    private AppointmentTypeCountDto appointmentTypes;
    private List<DoctorRatingDto> doctorRatings;
    private List<PaymentMethodStatsDto> paymentMethods;
}

