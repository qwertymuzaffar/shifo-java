package com.shifo.shifo_java.features.balance.converter;

import com.shifo.shifo_java.features.balance.model.EntityType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntityTypeConverterTest {

    private final EntityTypeConverter converter = new EntityTypeConverter();

    @Test
    void shouldReturnNullWhenSourceIsNull() {
        assertThat(converter.convert(null)).isNull();
    }

    @Test
    void shouldConvertLowercaseValue() {
        assertThat(converter.convert("payment")).isEqualTo(EntityType.PAYMENT);
    }

    @Test
    void shouldConvertUppercaseValue() {
        assertThat(converter.convert("TRANSACTION")).isEqualTo(EntityType.TRANSACTION);
    }

    @Test
    void shouldTrimWhitespace() {
        assertThat(converter.convert("  payment  ")).isEqualTo(EntityType.PAYMENT);
    }

    @Test
    void shouldThrowWithDescriptiveMessageOnInvalidValue() {
        assertThatThrownBy(() -> converter.convert("bogus"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid EntityType value: bogus");
    }
}
