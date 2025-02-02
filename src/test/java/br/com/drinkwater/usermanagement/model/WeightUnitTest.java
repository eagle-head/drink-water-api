package br.com.drinkwater.usermanagement.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static br.com.drinkwater.usermanagement.constants.UserManagementTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class WeightUnitTest {

    private static Stream<Arguments> validWeightUnitCodes() {
        return Stream.of(
            Arguments.of(WeightUnit.KG, VALID_WEIGHT_UNIT_KG_CODE)
        );
    }

    @ParameterizedTest
    @MethodSource("validWeightUnitCodes")
    public void givenValidCode_whenGetCode_thenReturnCorrectCode(WeightUnit unit, int expectedCode) {
        assertThat(unit.getCode()).isEqualTo(expectedCode);
    }

    @ParameterizedTest
    @MethodSource("validWeightUnitCodes")
    public void givenValidCode_whenFromCode_thenReturnCorrectWeightUnit(WeightUnit expectedUnit, int code) {
        assertThat(WeightUnit.fromCode(code)).isEqualTo(expectedUnit);
    }
    
    @Test
    public void givenInvalidCode_whenFromCode_thenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> WeightUnit.fromCode(INVALID_WEIGHT_UNIT_CODE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid WeightUnit code: " + INVALID_WEIGHT_UNIT_CODE);
    }
}