package com.shifo.shifo_java.features.payment.converter;

import com.shifo.shifo_java.features.payment.model.PaymentKind;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentKindConverterTest {

    private final PaymentKindConverter converter = new PaymentKindConverter();

    @Test
    void shouldConvertLowercaseValue() {
        assertThat(converter.convert("prepayment")).isEqualTo(PaymentKind.PREPAYMENT);
    }

    @Test
    void shouldConvertUppercaseValue() {
        assertThat(converter.convert("BALANCE_DEDUCTION")).isEqualTo(PaymentKind.BALANCE_DEDUCTION);
    }

    @Test
    void shouldThrowOnUnknownValue() {
        assertThatThrownBy(() -> converter.convert("bogus"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
