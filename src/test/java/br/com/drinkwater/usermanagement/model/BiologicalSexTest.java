package br.com.drinkwater.usermanagement.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static br.com.drinkwater.usermanagement.constants.UserManagementTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class BiologicalSexTest {

    private static Stream<Arguments> validBiologicalSexCodes() {
        return Stream.of(
                Arguments.of(BiologicalSex.MALE, VALID_BIOLOGICAL_SEX_MALE_CODE),
                Arguments.of(BiologicalSex.FEMALE, VALID_BIOLOGICAL_SEX_FEMALE_CODE)
        );
    }

    @ParameterizedTest
    @MethodSource("validBiologicalSexCodes")
    public void givenValidCode_whenGetCode_thenReturnCorrectCode(BiologicalSex sex, int expectedCode) {
        assertThat(sex.getCode()).isEqualTo(expectedCode);
    }

    @ParameterizedTest
    @MethodSource("validBiologicalSexCodes")
    public void givenValidCode_whenFromCode_thenReturnCorrectBiologicalSex(BiologicalSex expectedSex, int code) {
        assertThat(BiologicalSex.fromCode(code)).isEqualTo(expectedSex);
    }
    
    @Test
    public void givenInvalidCode_whenFromCode_thenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> BiologicalSex.fromCode(INVALID_BIOLOGICAL_SEX_CODE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid BiologicalSex code: " + INVALID_BIOLOGICAL_SEX_CODE);
    }
}