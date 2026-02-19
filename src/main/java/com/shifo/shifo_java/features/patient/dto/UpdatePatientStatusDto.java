package com.shifo.shifo_java.features.patient.dto;

import com.shifo.shifo_java.features.patient.PatientStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePatientStatusDto {

    private PatientStatus status;
}
