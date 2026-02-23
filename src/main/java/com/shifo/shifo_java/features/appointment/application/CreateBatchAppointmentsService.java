package com.shifo.shifo_java.features.appointment.application;

import com.shifo.shifo_java.features.appointment.application.command.CreateAppointmentCommand;
import com.shifo.shifo_java.features.appointment.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateBatchAppointmentsService {

    private final CreateSingleAppointmentService singleService;

    public CreateAppointmentResultDto create(CreateAppointmentDto dto) {

        List<AppointmentDto> successful = new ArrayList<>();
        List<FailedAppointmentDto> failed = new ArrayList<>();

        for (DateTimeDto datetime : dto.getDatetimes()) {

            try {
                AppointmentDto created = singleService.create(
                        new CreateAppointmentCommand(
                                dto.getDoctorId(),
                                dto.getPatientId(),
                                datetime.getDate(),
                                datetime.getTime(),
                                dto.getDuration(),
                                dto.getType(),
                                dto.getProcedureIds()
                        )
                );

                successful.add(created);

            } catch (Exception ex) {
                failed.add(new FailedAppointmentDto(
                        datetime.getDate(),
                        datetime.getTime(),
                        ex.getMessage()
                ));
            }
        }

        return new CreateAppointmentResultDto(successful, failed);
    }
}
