package br.com.drinkwater.usermanagement.model;

import static br.com.drinkwater.usermanagement.constants.PersonalTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

final class PersonalTest {

    @Test
    void givenValidParameters_whenConstructor_thenShouldCreatePersonal() {
        assertThat(PERSONAL).isNotNull();
        assertThat(PERSONAL.getFirstName()).isEqualTo(PERSONAL_DTO.firstName());
        assertThat(PERSONAL.getLastName()).isEqualTo(PERSONAL_DTO.lastName());
        assertThat(PERSONAL.getBirthDate()).isEqualTo(BIRTH_DATE);
        assertThat(PERSONAL.getBiologicalSex()).isEqualTo(PERSONAL_DTO.biologicalSex());
    }

    @Test
    void givenNullFirstName_whenConstructor_thenShouldThrowException() {
        assertThatThrownBy(
                        () ->
                                new Personal(
                                        null,
                                        PERSONAL_DTO.lastName(),
                                        BIRTH_DATE,
                                        PERSONAL_DTO.biologicalSex()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("First name cannot be null or empty");
    }

    @Test
    void givenEmptyFirstName_whenConstructor_thenShouldThrowException() {
        // When & Then
        assertThatThrownBy(
                        () ->
                                new Personal(
                                        "",
                                        PERSONAL_DTO.lastName(),
                                        BIRTH_DATE,
                                        PERSONAL_DTO.biologicalSex()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("First name cannot be null or empty");
    }

    @Test
    void givenNullLastName_whenConstructor_thenShouldThrowException() {
        // When & Then
        assertThatThrownBy(
                        () ->
                                new Personal(
                                        PERSONAL_DTO.firstName(),
                                        null,
                                        BIRTH_DATE,
                                        PERSONAL_DTO.biologicalSex()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Last name cannot be null or empty");
    }

    @Test
    void givenEmptyLastName_whenConstructor_thenShouldThrowException() {
        // When & Then
        assertThatThrownBy(
                        () ->
                                new Personal(
                                        PERSONAL_DTO.firstName(),
                                        "",
                                        BIRTH_DATE,
                                        PERSONAL_DTO.biologicalSex()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Last name cannot be null or empty");
    }

    @Test
    void givenNullBirthDate_whenConstructor_thenShouldThrowException() {
        // When & Then
        assertThatThrownBy(
                        () ->
                                new Personal(
                                        PERSONAL_DTO.firstName(),
                                        PERSONAL_DTO.lastName(),
                                        null,
                                        PERSONAL_DTO.biologicalSex()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Birth date cannot be null");
    }

    @Test
    void givenNullBiologicalSex_whenConstructor_thenShouldThrowException() {
        // When & Then
        assertThatThrownBy(
                        () ->
                                new Personal(
                                        PERSONAL_DTO.firstName(),
                                        PERSONAL_DTO.lastName(),
                                        BIRTH_DATE,
                                        null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Biological sex cannot be null");
    }

    @Test
    void givenSameInstance_whenEquals_thenShouldReturnTrue() {
        assertThat(PERSONAL.equals(PERSONAL)).isTrue();
    }

    @Test
    void givenTwoEqualPersonalObjects_whenEquals_thenShouldReturnTrue() {
        // Given
        Personal personal2 =
                new Personal(
                        PERSONAL_DTO.firstName(),
                        PERSONAL_DTO.lastName(),
                        BIRTH_DATE,
                        PERSONAL_DTO.biologicalSex());

        // When & Then
        assertThat(PERSONAL).isEqualTo(personal2);
        assertThat(PERSONAL.hashCode()).isEqualTo(personal2.hashCode());
    }

    @Test
    void givenPersonalAndNull_whenEquals_thenShouldReturnFalse() {
        assertThat(PERSONAL.equals(null)).isFalse();
    }

    @Test
    void givenDifferentObjectType_whenEquals_thenShouldReturnFalse() {
        assertThat(PERSONAL).isNotEqualTo("Not a Personal instance");
    }

    @Test
    void givenDifferentFirstName_whenEquals_thenShouldReturnFalse() {
        // Given
        Personal personal2 =
                new Personal(
                        "Jane", PERSONAL_DTO.lastName(), BIRTH_DATE, PERSONAL_DTO.biologicalSex());

        // When & Then
        assertThat(PERSONAL.equals(personal2)).isFalse();
    }

    @Test
    void givenDifferentLastName_whenEquals_thenShouldReturnFalse() {
        // Given
        Personal personal2 =
                new Personal(
                        PERSONAL_DTO.firstName(),
                        "Smith",
                        BIRTH_DATE,
                        PERSONAL_DTO.biologicalSex());

        // When & Then
        assertThat(PERSONAL.equals(personal2)).isFalse();
    }

    @Test
    void givenDifferentBirthDate_whenEquals_thenShouldReturnFalse() {
        // Given
        Personal personal2 =
                new Personal(
                        PERSONAL_DTO.firstName(),
                        PERSONAL_DTO.lastName(),
                        BIRTH_DATE.plusYears(5),
                        PERSONAL_DTO.biologicalSex());

        // When & Then
        assertThat(PERSONAL.equals(personal2)).isFalse();
    }

    @Test
    void givenDifferentBiologicalSex_whenEquals_thenShouldReturnFalse() {
        // Given
        Personal personal2 =
                new Personal(
                        PERSONAL_DTO.firstName(),
                        PERSONAL_DTO.lastName(),
                        BIRTH_DATE,
                        BiologicalSex.FEMALE);

        // When & Then
        assertThat(PERSONAL.equals(personal2)).isFalse();
    }

    @Test
    void givenPersonal_whenToString_thenShouldContainAllFields() {
        // When & Then
        assertThat(PERSONAL.toString())
                .contains("firstName='" + PERSONAL_DTO.firstName() + "'")
                .contains("lastName='" + PERSONAL_DTO.lastName() + "'")
                .contains("birthDate=" + BIRTH_DATE)
                .contains("biologicalSex=" + PERSONAL_DTO.biologicalSex());
    }

    @Test
    void givenDifferentPersonalObjects_whenHashCode_thenShouldReturnDifferentValues() {
        // Given
        Personal personal2 =
                new Personal(
                        "Jane", PERSONAL_DTO.lastName(), BIRTH_DATE, PERSONAL_DTO.biologicalSex());

        // When & Then
        assertThat(PERSONAL.hashCode()).isNotEqualTo(personal2.hashCode());
    }
}
