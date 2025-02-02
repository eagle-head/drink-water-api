package br.com.drinkwater.usermanagement.converter;

import br.com.drinkwater.usermanagement.model.HeightUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.drinkwater.usermanagement.constants.UserManagementTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class HeightUnitConverterTest {

    private HeightUnitConverter converter;

    @BeforeEach
    public void setUp() {
        this.converter = new HeightUnitConverter();
    }

    @Test
    public void givenCM_whenConvertToDatabaseColumn_thenReturnCMCode() {
        assertThat(this.converter.convertToDatabaseColumn(HeightUnit.CM))
                .isEqualTo(VALID_HEIGHT_UNIT_CM_CODE);
    }

    @Test
    public void givenNull_whenConvertToDatabaseColumn_thenReturnNull() {
        assertThat(this.converter.convertToDatabaseColumn(null))
                .isNull();
    }

    @Test
    public void givenCMCode_whenConvertToEntityAttribute_thenReturnCM() {
        assertThat(this.converter.convertToEntityAttribute(VALID_HEIGHT_UNIT_CM_CODE))
                .isEqualTo(HeightUnit.CM);
    }

    @Test
    public void givenNull_whenConvertToEntityAttribute_thenReturnNull() {
        assertThat(this.converter.convertToEntityAttribute(null))
                .isNull();
    }

    @Test
    public void givenInvalidCode_whenConvertToEntityAttribute_thenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> this.converter.convertToEntityAttribute(INVALID_HEIGHT_UNIT_CODE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid HeightUnit code: " + INVALID_HEIGHT_UNIT_CODE);
    }
}