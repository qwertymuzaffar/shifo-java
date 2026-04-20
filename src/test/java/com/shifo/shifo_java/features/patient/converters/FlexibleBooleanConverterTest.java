package com.shifo.shifo_java.features.patient.converters;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FlexibleBooleanConverterTest {

    private final FlexibleBooleanConverter converter = new FlexibleBooleanConverter();

    @Test
    void shouldReturnNullForNullAndBlank() {
        assertThat(converter.convert(null)).isNull();
        assertThat(converter.convert("")).isNull();
        assertThat(converter.convert("   ")).isNull();
    }

    @Test
    void shouldParseTruthyValues() {
        assertThat(converter.convert("true")).isTrue();
        assertThat(converter.convert("TRUE")).isTrue();
        assertThat(converter.convert(" True ")).isTrue();
        assertThat(converter.convert("1")).isTrue();
    }

    @Test
    void shouldParseFalsyValues() {
        assertThat(converter.convert("false")).isFalse();
        assertThat(converter.convert("FALSE")).isFalse();
        assertThat(converter.convert("0")).isFalse();
    }

    @Test
    void shouldThrowOnInvalidValue() {
        assertThatThrownBy(() -> converter.convert("yes"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("true/false/1/0");
    }
}
