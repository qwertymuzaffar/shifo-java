package com.shifo.shifo_java.features.patient.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FlexibleBooleanConverter implements Converter<String, Boolean> {

    @Override
    public Boolean convert(String value) {
        if (value == null || value.isBlank()) return null;

        return switch (value.trim().toLowerCase()) {
            case "true", "1" -> true;
            case "false", "0" -> false;
            default -> throw new IllegalArgumentException(
                    "Boolean value must be true/false/1/0"
            );
        };
    }
}

