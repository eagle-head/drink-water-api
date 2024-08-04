package br.com.drinkwater.drinkwaterapi.model;

import br.com.drinkwater.drinkwaterapi.usermanagement.model.BiologicalSex;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.BiologicalSexConverter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BiologicalSexConverterTest {

    private final BiologicalSexConverter converter = new BiologicalSexConverter();

    @Test
    public void convertToDatabaseColumn_ValidBiologicalSex_ReturnsCorrectCode() {
        assertThat(converter.convertToDatabaseColumn(BiologicalSex.MALE)).isEqualTo(1);
        assertThat(converter.convertToDatabaseColumn(BiologicalSex.FEMALE)).isEqualTo(2);
    }

    @Test
    public void convertToDatabaseColumn_NullBiologicalSex_ReturnsNull() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    public void convertToEntityAttribute_ValidCode_ReturnsCorrectBiologicalSex() {
        assertThat(converter.convertToEntityAttribute(1)).isEqualTo(BiologicalSex.MALE);
        assertThat(converter.convertToEntityAttribute(2)).isEqualTo(BiologicalSex.FEMALE);
    }

    @Test
    public void convertToEntityAttribute_NullCode_ReturnsNull() {
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    public void convertToEntityAttribute_InvalidCode_ThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> converter.convertToEntityAttribute(3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid BiologicalSex code: 3");
    }
}
