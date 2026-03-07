package com.shifo.shifo_java.features.payment.converter;

import com.shifo.shifo_java.features.payment.model.PaymentType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PaymentTypeConverter implements Converter<String, PaymentType> {

    @Override
    public PaymentType convert(String source) {
        return PaymentType.fromValue(source);
    }
}
