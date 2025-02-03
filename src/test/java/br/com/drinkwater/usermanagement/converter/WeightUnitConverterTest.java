package br.com.drinkwater.usermanagement.converter;

import br.com.drinkwater.usermanagement.model.WeightUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class WeightUnitConverterTest {

    private static final WeightUnit VALID_KG_ENTITY = WeightUnit.KG;
    private static final Integer VALID_KG_DB = 1;
    private static final Integer INVALID_DB_CODE = 999;
    private static final WeightUnit NULL_ENTITY = null;
    private static final Integer NULL_DB = null;

    private WeightUnitConverter converter;

    @BeforeEach
    public void setUp() {
        this.converter = new WeightUnitConverter();
    }

    @Test
    public void givenKG_whenConvertToDatabaseColumn_thenReturnKGCode() {
        assertThat(this.converter.convertToDatabaseColumn(VALID_KG_ENTITY))
                .isEqualTo(VALID_KG_DB);
    }

    @Test
    public void givenNull_whenConvertToDatabaseColumn_thenReturnNull() {
        assertThat(this.converter.convertToDatabaseColumn(NULL_ENTITY))
                .isEqualTo(NULL_DB);
    }

    @Test
    public void givenKGCode_whenConvertToEntityAttribute_thenReturnKG() {
        assertThat(this.converter.convertToEntityAttribute(VALID_KG_DB))
                .isEqualTo(VALID_KG_ENTITY);
    }

    @Test
    public void givenNull_whenConvertToEntityAttribute_thenReturnNull() {
        assertThat(this.converter.convertToEntityAttribute(NULL_DB))
                .isEqualTo(NULL_ENTITY);
    }

    @Test
    public void givenInvalidCode_whenConvertToEntityAttribute_thenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> this.converter.convertToEntityAttribute(INVALID_DB_CODE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid WeightUnit code: " + INVALID_DB_CODE);
    }
}