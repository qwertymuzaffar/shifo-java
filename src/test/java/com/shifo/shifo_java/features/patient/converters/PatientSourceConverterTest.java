package com.shifo.shifo_java.features.patient.converters;

import com.shifo.shifo_java.features.patient.enums.PatientSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PatientSourceConverterTest {

    private final PatientSourceConverter converter = new PatientSourceConverter();

    @Test
    void shouldConvertLowercaseValue() {
        assertThat(converter.convert("telegram")).isEqualTo(PatientSource.TELEGRAM);
    }

    @Test
    void shouldConvertUppercaseValue() {
        assertThat(converter.convert("MANUAL")).isEqualTo(PatientSource.MANUAL);
    }

    @Test
    void shouldThrowOnInvalidValue() {
        assertThatThrownBy(() -> converter.convert("bogus"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
