package br.com.drinkwater.usermanagement.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static br.com.drinkwater.usermanagement.constants.PersonalTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class PersonalTest {

    @ParameterizedTest(name = "Testing setter/getter with value: {2}")
    @MethodSource("provideSetterGetterPairs")
    public <T> void testGetterSetter(final BiConsumer<Personal, T> setter,
                                     final Function<Personal, T> getter,
                                     final T expectedValue) {
        setter.accept(PERSONAL, expectedValue);
        assertThat(getter.apply(PERSONAL)).isEqualTo(expectedValue);
    }

    public static Stream<Arguments> provideSetterGetterPairs() {
        return Stream.of(
                Arguments.of((BiConsumer<Personal, String>) Personal::setFirstName,
                        (Function<Personal, String>) Personal::getFirstName,
                        PERSONAL_DTO.firstName()),
                Arguments.of((BiConsumer<Personal, String>) Personal::setLastName,
                        (Function<Personal, String>) Personal::getLastName,
                        PERSONAL_DTO.lastName()),
                Arguments.of((BiConsumer<Personal, LocalDate>) Personal::setBirthDate,
                        (Function<Personal, LocalDate>) Personal::getBirthDate,
                        BIRTH_DATE),
                Arguments.of((BiConsumer<Personal, BiologicalSex>) Personal::setBiologicalSex,
                        (Function<Personal, BiologicalSex>) Personal::getBiologicalSex,
                        PERSONAL_DTO.biologicalSex())
        );
    }

    @Test
    public void givenValidParameters_whenConstructor_thenShouldCreatePersonal() {
        assertThat(PERSONAL).isNotNull();
        assertThat(PERSONAL.getFirstName()).isEqualTo(PERSONAL_DTO.firstName());
        assertThat(PERSONAL.getLastName()).isEqualTo(PERSONAL_DTO.lastName());
        assertThat(PERSONAL.getBirthDate()).isEqualTo(BIRTH_DATE);
        assertThat(PERSONAL.getBiologicalSex()).isEqualTo(PERSONAL_DTO.biologicalSex());
    }

    @Test
    public void givenNullFirstName_whenConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new Personal(
                        null,
                        PERSONAL_DTO.lastName(),
                        BIRTH_DATE,
                        PERSONAL_DTO.biologicalSex()
                ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("First name cannot be null or empty");
    }

    @Test
    public void givenEmptyFirstName_whenConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new Personal(
                        "",
                        PERSONAL_DTO.lastName(),
                        BIRTH_DATE,
                        PERSONAL_DTO.biologicalSex()
                ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("First name cannot be null or empty");
    }

    @Test
    public void givenNullLastName_whenConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new Personal(
                        PERSONAL_DTO.firstName(),
                        null,
                        BIRTH_DATE,
                        PERSONAL_DTO.biologicalSex()
                ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Last name cannot be null or empty");
    }

    @Test
    public void givenEmptyLastName_whenConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new Personal(
                        PERSONAL_DTO.firstName(),
                        "",
                        BIRTH_DATE,
                        PERSONAL_DTO.biologicalSex()
                ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Last name cannot be null or empty");
    }

    @Test
    public void givenNullBirthDate_whenConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new Personal(
                        PERSONAL_DTO.firstName(),
                        PERSONAL_DTO.lastName(),
                        null,
                        PERSONAL_DTO.biologicalSex()
                ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Birth date cannot be null");
    }

    @Test
    public void givenNullBiologicalSex_whenConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new Personal(
                        PERSONAL_DTO.firstName(),
                        PERSONAL_DTO.lastName(),
                        BIRTH_DATE,
                        null
                ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Biological sex cannot be null");
    }

    @Test
    public void givenSameInstance_whenEquals_thenShouldReturnTrue() {
        assertThat(PERSONAL.equals(PERSONAL)).isTrue();
    }

    @Test
    public void givenTwoEqualPersonalObjects_whenEquals_thenShouldReturnTrue() {
        Personal personal2 = new Personal(
                PERSONAL_DTO.firstName(),
                PERSONAL_DTO.lastName(),
                BIRTH_DATE,
                PERSONAL_DTO.biologicalSex()
        );

        assertThat(PERSONAL).isEqualTo(personal2);
        assertThat(PERSONAL.hashCode()).isEqualTo(personal2.hashCode());
    }

    @Test
    public void givenPersonalAndNull_whenEquals_thenShouldReturnFalse() {
        assertThat(PERSONAL.equals(null)).isFalse();
    }

    @Test
    public void givenDifferentObjectType_whenEquals_thenShouldReturnFalse() {
        assertThat(PERSONAL.equals("Not a Personal instance")).isFalse();
    }

    @Test
    public void givenDifferentFirstName_whenEquals_thenShouldReturnFalse() {
        Personal personal2 = new Personal(
                "Jane",
                PERSONAL_DTO.lastName(),
                BIRTH_DATE,
                PERSONAL_DTO.biologicalSex()
        );

        assertThat(PERSONAL.equals(personal2)).isFalse();
    }

    @Test
    public void givenDifferentLastName_whenEquals_thenShouldReturnFalse() {
        Personal personal2 = new Personal(
                PERSONAL_DTO.firstName(),
                "Smith",
                BIRTH_DATE,
                PERSONAL_DTO.biologicalSex()
        );

        assertThat(PERSONAL.equals(personal2)).isFalse();
    }

    @Test
    public void givenDifferentBirthDate_whenEquals_thenShouldReturnFalse() {
        Personal personal2 = new Personal(
                PERSONAL_DTO.firstName(),
                PERSONAL_DTO.lastName(),
                BIRTH_DATE.plusYears(5),
                PERSONAL_DTO.biologicalSex()
        );

        assertThat(PERSONAL.equals(personal2)).isFalse();
    }

    @Test
    public void givenDifferentBiologicalSex_whenEquals_thenShouldReturnFalse() {
        Personal personal2 = new Personal(
                PERSONAL_DTO.firstName(),
                PERSONAL_DTO.lastName(),
                BIRTH_DATE,
                BiologicalSex.FEMALE
        );

        assertThat(PERSONAL.equals(personal2)).isFalse();
    }

    @Test
    public void givenPersonal_whenToString_thenShouldContainAllFields() {
        assertThat(PERSONAL.toString()).contains("firstName='" + PERSONAL_DTO.firstName() + "'")
                .contains("lastName='" + PERSONAL_DTO.lastName() + "'")
                .contains("birthDate=" + BIRTH_DATE)
                .contains("biologicalSex=" + PERSONAL_DTO.biologicalSex());
    }

    @Test
    public void givenDifferentPersonalObjects_whenHashCode_thenShouldReturnDifferentValues() {
        Personal personal2 = new Personal(
                "Jane",
                PERSONAL_DTO.lastName(),
                BIRTH_DATE,
                PERSONAL_DTO.biologicalSex()
        );

        assertThat(PERSONAL.hashCode()).isNotEqualTo(personal2.hashCode());
    }
}