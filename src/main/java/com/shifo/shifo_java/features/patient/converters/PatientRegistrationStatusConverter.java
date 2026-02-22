package com.shifo.shifo_java.features.patient.converters;

import com.shifo.shifo_java.features.patient.enums.PatientRegistrationStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PatientRegistrationStatusConverter
        implements Converter<String, PatientRegistrationStatus> {

    @Override
    public PatientRegistrationStatus convert(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return PatientRegistrationStatus.fromValue(value);
    }
}
