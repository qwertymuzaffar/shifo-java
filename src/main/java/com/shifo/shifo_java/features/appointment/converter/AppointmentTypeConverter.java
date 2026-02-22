package com.shifo.shifo_java.features.appointment.converter;

import com.shifo.shifo_java.features.appointment.model.AppointmentType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AppointmentTypeConverter implements AttributeConverter<AppointmentType, String> {

    @Override
    public String convertToDatabaseColumn(AppointmentType attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public AppointmentType convertToEntityAttribute(String dbData) {
        return dbData != null ? AppointmentType.fromValue(dbData) : null;
    }
}
