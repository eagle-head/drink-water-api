package br.com.drinkwater.hydrationtracking.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class VolumeUnitTest {

    private static final int INVALID_VOLUME_UNIT_CODE = -1;

    private static Stream<Arguments> validVolumeUnitCodes() {
        return Stream.of(Arguments.of(VolumeUnit.ML, 1));
    }

    @ParameterizedTest
    @MethodSource("validVolumeUnitCodes")
    public void givenValidCode_whenGetCode_thenReturnCorrectCode(VolumeUnit unit, int expectedCode) {
        assertThat(unit.getCode()).isEqualTo(expectedCode);
    }

    @ParameterizedTest
    @MethodSource("validVolumeUnitCodes")
    public void givenValidCode_whenFromCode_thenReturnCorrectVolumeUnit(VolumeUnit expectedUnit, int code) {
        assertThat(VolumeUnit.fromCode(code)).isEqualTo(expectedUnit);
    }

    @Test
    public void givenInvalidCode_whenFromCode_thenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> VolumeUnit.fromCode(INVALID_VOLUME_UNIT_CODE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid VolumeUnit code: " + INVALID_VOLUME_UNIT_CODE);
    }
}