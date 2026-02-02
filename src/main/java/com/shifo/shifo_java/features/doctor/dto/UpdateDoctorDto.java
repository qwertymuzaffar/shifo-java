package com.shifo.shifo_java.features.doctor.dto;

import com.shifo.shifo_java.features.specialization.Specialization;
import com.shifo.shifo_java.features.user.dto.UpdateUserDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateDoctorDto extends UpdateUserDto {

    private Specialization specialization;

    private Long roomId;

    private Integer experience;

    private Integer consultationFee;

    private WorkingHoursDto workingHours;
}

