package com.shifo.shifo_java.features.payment.converter;

import com.shifo.shifo_java.features.payment.model.PaymentType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentTypeConverterTest {

    private final PaymentTypeConverter converter = new PaymentTypeConverter();

    @Test
    void shouldConvertLowercaseValue() {
        assertThat(converter.convert("cash")).isEqualTo(PaymentType.CASH);
    }

    @Test
    void shouldConvertUppercaseValue() {
        assertThat(converter.convert("CARD")).isEqualTo(PaymentType.CARD);
    }

    @Test
    void shouldThrowOnUnknownValue() {
        assertThatThrownBy(() -> converter.convert("unknown"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
