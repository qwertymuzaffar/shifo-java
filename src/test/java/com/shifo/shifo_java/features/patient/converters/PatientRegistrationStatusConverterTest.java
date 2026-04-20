package com.shifo.shifo_java.features.patient.converters;

import com.shifo.shifo_java.features.patient.enums.PatientRegistrationStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PatientRegistrationStatusConverterTest {

    private final PatientRegistrationStatusConverter converter = new PatientRegistrationStatusConverter();

    @Test
    void shouldReturnNullForNullAndBlank() {
        assertThat(converter.convert(null)).isNull();
        assertThat(converter.convert("")).isNull();
        assertThat(converter.convert("  ")).isNull();
    }

    @Test
    void shouldConvertValidValueCaseInsensitive() {
        assertThat(converter.convert("approved")).isEqualTo(PatientRegistrationStatus.APPROVED);
        assertThat(converter.convert("PENDING")).isEqualTo(PatientRegistrationStatus.PENDING);
    }

    @Test
    void shouldThrowOnInvalidValue() {
        assertThatThrownBy(() -> converter.convert("bogus"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
