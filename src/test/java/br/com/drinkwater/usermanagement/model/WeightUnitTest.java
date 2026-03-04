package br.com.drinkwater.usermanagement.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

final class WeightUnitTest {

    private static final int INVALID_WEIGHT_UNIT_CODE = -1;

    private static Stream<Arguments> validWeightUnitCodes() {
        return Stream.of(Arguments.of(WeightUnit.KG, 1));
    }

    @ParameterizedTest
    @MethodSource("validWeightUnitCodes")
    void givenValidCode_whenGetCode_thenReturnCorrectCode(WeightUnit unit, int expectedCode) {
        assertThat(unit.getCode()).isEqualTo(expectedCode);
    }

    @ParameterizedTest
    @MethodSource("validWeightUnitCodes")
    void givenValidCode_whenFromCode_thenReturnCorrectWeightUnit(
            WeightUnit expectedUnit, int code) {
        assertThat(WeightUnit.fromCode(code)).isEqualTo(expectedUnit);
    }

    @Test
    void givenInvalidCode_whenFromCode_thenThrowIllegalArgumentException() {
        // When & Then
        assertThatThrownBy(() -> WeightUnit.fromCode(INVALID_WEIGHT_UNIT_CODE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid WeightUnit code: " + INVALID_WEIGHT_UNIT_CODE);
    }
}
