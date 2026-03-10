package com.shifo.shifo_java.features.balance.converter;

import com.shifo.shifo_java.features.balance.model.EntityType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EntityTypeConverter implements Converter<String, EntityType> {

    @Override
    public EntityType convert(String source) {
        if (source == null) {
            return null;
        }

        try {
            return EntityType.valueOf(source.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Invalid EntityType value: " + source
            );
        }
    }
}
