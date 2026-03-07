package com.shifo.shifo_java.features.payment.converter;

import com.shifo.shifo_java.features.payment.model.PaymentKind;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PaymentKindConverter implements Converter<String, PaymentKind> {

    @Override
    public PaymentKind convert(String source) {
        return PaymentKind.fromValue(source);
    }
}
