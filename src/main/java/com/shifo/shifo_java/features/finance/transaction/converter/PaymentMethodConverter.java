package com.shifo.shifo_java.features.finance.transaction.converter;

import com.shifo.shifo_java.features.payment.model.PaymentType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentMethodConverter implements AttributeConverter<PaymentType, String> {

    @Override
    public String convertToDatabaseColumn(PaymentType attribute) {
        if (attribute == null) return null;
        return attribute.getValue();
    }

    @Override
    public PaymentType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return PaymentType.fromValue(dbData);
    }
}
