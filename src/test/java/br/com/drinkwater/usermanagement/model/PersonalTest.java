package br.com.drinkwater.usermanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

public final class PersonalTest {

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final OffsetDateTime BIRTH_DATE = OffsetDateTime
            .of(1990, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime PAST_BIRTH_DATE = OffsetDateTime
            .of(1980, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final BiologicalSex BIOLOGICAL_SEX = BiologicalSex.MALE;

    private Personal personal;

    @BeforeEach
    public void setUp() {
        personal = new Personal();
    }

    @Test
    public void givenFirstName_whenSetFirstName_thenGetFirstName() {
        personal.setFirstName(FIRST_NAME);
        assertThat(personal.getFirstName()).isEqualTo(FIRST_NAME);
    }

    @Test
    public void givenLastName_whenSetLastName_thenGetLastName() {
        personal.setLastName(LAST_NAME);
        assertThat(personal.getLastName()).isEqualTo(LAST_NAME);
    }

    @Test
    public void givenBirthDate_whenSetBirthDate_thenGetBirthDate() {
        personal.setBirthDate(BIRTH_DATE);
        assertThat(personal.getBirthDate()).isEqualTo(BIRTH_DATE);
    }

    @Test
    public void givenBiologicalSex_whenSetBiologicalSex_thenGetBiologicalSex() {
        personal.setBiologicalSex(BIOLOGICAL_SEX);
        assertThat(personal.getBiologicalSex()).isEqualTo(BIOLOGICAL_SEX);
    }

    @Test
    public void givenPastDate_whenSetBirthDate_thenGetPastDate() {
        personal.setBirthDate(PAST_BIRTH_DATE);
        assertThat(personal.getBirthDate()).isEqualTo(PAST_BIRTH_DATE);
    }

    @Test
    public void givenAllFields_whenSettingAllFields_thenAllFieldsAreSet() {
        personal.setFirstName(FIRST_NAME);
        personal.setLastName(LAST_NAME);
        personal.setBirthDate(BIRTH_DATE);
        personal.setBiologicalSex(BIOLOGICAL_SEX);

        assertThat(personal.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(personal.getLastName()).isEqualTo(LAST_NAME);
        assertThat(personal.getBirthDate()).isEqualTo(BIRTH_DATE);
        assertThat(personal.getBiologicalSex()).isEqualTo(BIOLOGICAL_SEX);
    }
}