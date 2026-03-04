package br.com.drinkwater.usermanagement.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

final class HeightUnitTest {

    private static final int INVALID_HEIGHT_UNIT_CODE = -1;

    private static Stream<Arguments> validHeightUnitCodes() {
        return Stream.of(Arguments.of(HeightUnit.CM, 1));
    }

    @ParameterizedTest
    @MethodSource("validHeightUnitCodes")
    void givenValidCode_whenGetCode_thenReturnCorrectCode(HeightUnit unit, int expectedCode) {
        assertThat(unit.getCode()).isEqualTo(expectedCode);
    }

    @ParameterizedTest
    @MethodSource("validHeightUnitCodes")
    void givenValidCode_whenFromCode_thenReturnCorrectHeightUnit(
            HeightUnit expectedUnit, int code) {
        assertThat(HeightUnit.fromCode(code)).isEqualTo(expectedUnit);
    }

    @Test
    void givenInvalidCode_whenFromCode_thenThrowIllegalArgumentException() {
        // When & Then
        assertThatThrownBy(() -> HeightUnit.fromCode(INVALID_HEIGHT_UNIT_CODE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid HeightUnit code: " + INVALID_HEIGHT_UNIT_CODE);
    }
}
