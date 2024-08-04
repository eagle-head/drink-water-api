package br.com.drinkwater.drinkwaterapi.model;

import br.com.drinkwater.drinkwaterapi.usermanagement.model.WeightUnit;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.WeightUnitConverter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class WeightUnitConverterTest {

    private final WeightUnitConverter converter = new WeightUnitConverter();

    @Test
    public void convertToDatabaseColumn_ValidWeightUnit_ReturnsCorrectCode() {
        assertThat(converter.convertToDatabaseColumn(WeightUnit.KG)).isEqualTo(1);
    }

    @Test
    public void convertToDatabaseColumn_NullWeightUnit_ReturnsNull() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    public void convertToEntityAttribute_ValidCode_ReturnsCorrectWeightUnit() {
        assertThat(converter.convertToEntityAttribute(1)).isEqualTo(WeightUnit.KG);
    }

    @Test
    public void convertToEntityAttribute_NullCode_ReturnsNull() {
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    public void convertToEntityAttribute_InvalidCode_ThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> converter.convertToEntityAttribute(2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid WeightUnit code: 2");
    }
}
