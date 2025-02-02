package br.com.drinkwater.usermanagement.converter;

import br.com.drinkwater.usermanagement.model.WeightUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.drinkwater.usermanagement.constants.UserManagementTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class WeightUnitConverterTest {

    private WeightUnitConverter converter;

    @BeforeEach
    public void setUp() {
        this.converter = new WeightUnitConverter();
    }

    @Test
    public void givenKG_whenConvertToDatabaseColumn_thenReturnKGCode() {
        assertThat(this.converter.convertToDatabaseColumn(WeightUnit.KG))
                .isEqualTo(VALID_WEIGHT_UNIT_KG_CODE);
    }

    @Test
    public void givenNull_whenConvertToDatabaseColumn_thenReturnNull() {
        assertThat(this.converter.convertToDatabaseColumn(null))
                .isNull();
    }

    @Test
    public void givenKGCode_whenConvertToEntityAttribute_thenReturnKG() {
        assertThat(this.converter.convertToEntityAttribute(VALID_WEIGHT_UNIT_KG_CODE))
                .isEqualTo(WeightUnit.KG);
    }

    @Test
    public void givenNull_whenConvertToEntityAttribute_thenReturnNull() {
        assertThat(this.converter.convertToEntityAttribute(null))
                .isNull();
    }

    @Test
    public void givenInvalidCode_whenConvertToEntityAttribute_thenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> this.converter.convertToEntityAttribute(INVALID_WEIGHT_UNIT_CODE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid WeightUnit code: " + INVALID_WEIGHT_UNIT_CODE);
    }
}