package com.shifo.shifo_java.features.patient.converters;

import com.shifo.shifo_java.features.patient.enums.PatientSortField;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PatientSortFieldConverterTest {

    private final PatientSortFieldConverter converter = new PatientSortFieldConverter();

    @Test
    void shouldConvertLowercaseValue() {
        assertThat(converter.convert("balance")).isEqualTo(PatientSortField.BALANCE);
    }

    @Test
    void shouldConvertUppercaseValue() {
        assertThat(converter.convert("FULLNAME")).isEqualTo(PatientSortField.FULLNAME);
    }

    @Test
    void shouldThrowOnInvalidValue() {
        assertThatThrownBy(() -> converter.convert("unknown"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
