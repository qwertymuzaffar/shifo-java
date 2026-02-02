package com.shifo.shifo_java.features.doctor.dto;

import com.shifo.shifo_java.features.specialization.Specialization;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorUpdateFieldsDto {

    private Specialization specialization;

    private Long roomId;

    private Integer experience;

    private Integer consultationFee;

    private WorkingHoursDto workingHours;
}

