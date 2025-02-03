package br.com.drinkwater.usermanagement.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public final class PersonalTest {

    private static final String FIRST_NAME = "John";
    private static final String NEW_FIRST_NAME = "Jane";
    private static final String LAST_NAME = "Doe";
    private static final String NEW_LAST_NAME = "Smith";
    private static final OffsetDateTime BIRTH_DATE = OffsetDateTime
            .of(1990, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime NEW_BIRTH_DATE = OffsetDateTime
            .of(1995, 5, 15, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final BiologicalSex BIOLOGICAL_SEX = BiologicalSex.MALE;
    private static final BiologicalSex NEW_BIOLOGICAL_SEX = BiologicalSex.FEMALE;

    @ParameterizedTest(name = "Testing setter/getter with value: {2}")
    @MethodSource("provideSetterGetterPairs")
    public <T> void testGetterSetter(final BiConsumer<Personal, T> setter,
                                     final Function<Personal, T> getter,
                                     final T expectedValue) {
        Personal personal = new Personal();
        setter.accept(personal, expectedValue);

        assertThat(getter.apply(personal)).isEqualTo(expectedValue);
    }

    public static Stream<Arguments> provideSetterGetterPairs() {
        return Stream.of(
                Arguments.of((BiConsumer<Personal, String>) Personal::setFirstName,
                        (Function<Personal, String>) Personal::getFirstName,
                        FIRST_NAME),
                Arguments.of((BiConsumer<Personal, String>) Personal::setLastName,
                        (Function<Personal, String>) Personal::getLastName,
                        LAST_NAME),
                Arguments.of((BiConsumer<Personal, OffsetDateTime>) Personal::setBirthDate,
                        (Function<Personal, OffsetDateTime>) Personal::getBirthDate,
                        BIRTH_DATE),
                Arguments.of((BiConsumer<Personal, BiologicalSex>) Personal::setBiologicalSex,
                        (Function<Personal, BiologicalSex>) Personal::getBiologicalSex,
                        BIOLOGICAL_SEX)
        );
    }

    private Personal createDefaultPersonal() {
        var personal = new Personal();
        personal.setFirstName(FIRST_NAME);
        personal.setLastName(LAST_NAME);
        personal.setBirthDate(BIRTH_DATE);
        personal.setBiologicalSex(BIOLOGICAL_SEX);

        return personal;
    }

    @Test
    public void givenNewPersonal_whenInstantiated_thenShouldNotBeNull() {
        var personal = new Personal();

        assertThat(personal).isNotNull();
    }

    @Test
    public void givenSameInstance_whenEquals_thenShouldReturnTrue() {
        var personal = createDefaultPersonal();

        assertThat(personal.equals(personal)).isTrue();
    }

    @Test
    public void givenTwoEqualPersonalObjects_whenEquals_thenShouldReturnTrue() {
        var personal1 = createDefaultPersonal();
        var personal2 = createDefaultPersonal();

        assertThat(personal1).isEqualTo(personal2);
        assertThat(personal1.hashCode()).isEqualTo(personal2.hashCode());
    }

    @Test
    public void givenPersonalAndNull_whenEquals_thenShouldReturnFalse() {
        var personal = createDefaultPersonal();

        assertThat(personal.equals(null)).isFalse();
    }

    @Test
    public void givenDifferentObjectType_whenEquals_thenShouldReturnFalse() {
        var personal = createDefaultPersonal();
        var differentType = "Not a Personal instance";

        assertThat(personal.equals(differentType)).isFalse();
    }

    @Test
    public void givenDifferentFirstName_whenEquals_thenShouldReturnFalse() {
        var personal1 = createDefaultPersonal();
        var personal2 = createDefaultPersonal();
        personal2.setFirstName(NEW_FIRST_NAME);

        assertThat(personal1.equals(personal2)).isFalse();
    }

    @Test
    public void givenDifferentLastName_whenEquals_thenShouldReturnFalse() {
        var personal1 = createDefaultPersonal();
        var personal2 = createDefaultPersonal();
        personal2.setLastName(NEW_LAST_NAME);

        assertThat(personal1.equals(personal2)).isFalse();
    }

    @Test
    public void givenDifferentBirthDate_whenEquals_thenShouldReturnFalse() {
        var personal1 = createDefaultPersonal();
        var personal2 = createDefaultPersonal();
        personal2.setBirthDate(NEW_BIRTH_DATE);

        assertThat(personal1.equals(personal2)).isFalse();
    }

    @Test
    public void givenDifferentBiologicalSex_whenEquals_thenShouldReturnFalse() {
        var personal1 = createDefaultPersonal();
        var personal2 = createDefaultPersonal();
        personal2.setBiologicalSex(NEW_BIOLOGICAL_SEX);

        assertThat(personal1.equals(personal2)).isFalse();
    }

    @Test
    public void givenPersonal_whenToString_thenShouldContainAllFields() {
        var personal = createDefaultPersonal();
        var toString = personal.toString();

        assertThat(toString).contains("firstName='" + FIRST_NAME + "'")
                .contains("lastName='" + LAST_NAME + "'")
                .contains("birthDate=" + BIRTH_DATE)
                .contains("biologicalSex=" + BIOLOGICAL_SEX);
    }

    @Test
    public void givenPersonalWithNullFields_whenToString_thenShouldHandleNull() {
        var personal = new Personal();
        personal.setFirstName(null);
        personal.setLastName(null);
        personal.setBirthDate(null);
        personal.setBiologicalSex(null);
        var toString = personal.toString();

        assertThat(toString).contains("firstName='null'")
                .contains("lastName='null'")
                .contains("birthDate=null")
                .contains("biologicalSex=null");
    }

    @Test
    public void givenDifferentPersonalObjects_whenHashCode_thenShouldReturnDifferentValues() {
        var personal1 = createDefaultPersonal();
        var personal2 = createDefaultPersonal();
        personal2.setFirstName(NEW_FIRST_NAME);

        assertThat(personal1.hashCode()).isNotEqualTo(personal2.hashCode());
    }
}