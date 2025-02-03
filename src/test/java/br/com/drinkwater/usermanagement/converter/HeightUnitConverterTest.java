package br.com.drinkwater.usermanagement.converter;

import br.com.drinkwater.usermanagement.model.HeightUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class HeightUnitConverterTest {

    private static final HeightUnit VALID_CM_ENTITY = HeightUnit.CM;
    private static final Integer VALID_CM_DB = 1;
    private static final Integer INVALID_DB_CODE = 999;
    private static final HeightUnit NULL_ENTITY = null;
    private static final Integer NULL_DB = null;

    private HeightUnitConverter converter;

    @BeforeEach
    public void setUp() {
        this.converter = new HeightUnitConverter();
    }

    @Test
    public void givenCM_whenConvertToDatabaseColumn_thenReturnCMCode() {
        assertThat(this.converter.convertToDatabaseColumn(VALID_CM_ENTITY))
                .isEqualTo(VALID_CM_DB);
    }

    @Test
    public void givenNull_whenConvertToDatabaseColumn_thenReturnNull() {
        assertThat(this.converter.convertToDatabaseColumn(NULL_ENTITY))
                .isEqualTo(NULL_DB);
    }

    @Test
    public void givenCMCode_whenConvertToEntityAttribute_thenReturnCM() {
        assertThat(this.converter.convertToEntityAttribute(VALID_CM_DB))
                .isEqualTo(VALID_CM_ENTITY);
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
                .hasMessage("Invalid HeightUnit code: " + INVALID_DB_CODE);
    }
}