package br.com.drinkwater.drinkwaterapi.model;

import br.com.drinkwater.drinkwaterapi.usermanagement.model.HeightUnit;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.HeightUnitConverter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class HeightUnitConverterTest {

    private final HeightUnitConverter converter = new HeightUnitConverter();

    @Test
    public void convertToDatabaseColumn_ValidHeightUnit_ReturnsCorrectCode() {
        assertThat(converter.convertToDatabaseColumn(HeightUnit.CM)).isEqualTo(1);
    }

    @Test
    public void convertToDatabaseColumn_NullHeightUnit_ReturnsNull() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    public void convertToEntityAttribute_ValidCode_ReturnsCorrectHeightUnit() {
        assertThat(converter.convertToEntityAttribute(1)).isEqualTo(HeightUnit.CM);
    }

    @Test
    public void convertToEntityAttribute_NullCode_ReturnsNull() {
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    public void convertToEntityAttribute_InvalidCode_ThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> converter.convertToEntityAttribute(2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid HeightUnit code: 2");
    }
}
