package com.shifo.shifo_java.features.patient.converters;

import com.shifo.shifo_java.features.patient.enums.PatientSortField;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PatientSortFieldConverter
        implements Converter<String, PatientSortField> {

    @Override
    public PatientSortField convert(String value) {
        return PatientSortField.valueOf(value.toUpperCase());
    }
}

