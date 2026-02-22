package com.shifo.shifo_java.features.patient.converters;

import com.shifo.shifo_java.features.patient.enums.PatientSource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PatientSourceConverter implements Converter<String, PatientSource> {

    @Override
    public PatientSource convert(String value) {
        return PatientSource.fromValue(value);
    }
}

