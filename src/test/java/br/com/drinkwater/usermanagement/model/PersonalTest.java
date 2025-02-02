package br.com.drinkwater.usermanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.drinkwater.usermanagement.constants.UserManagementTestData.*;
import static org.assertj.core.api.Assertions.assertThat;

public final class PersonalTest {

    private Personal personal;

    @BeforeEach
    public void setUp() {
        personal = new Personal();
    }

    @Test
    public void givenFirstName_whenSetFirstName_thenGetFirstName() {
        personal.setFirstName(DEFAULT_FIRST_NAME);
        assertThat(personal.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
    }

    @Test
    public void givenLastName_whenSetLastName_thenGetLastName() {
        personal.setLastName(DEFAULT_LAST_NAME);
        assertThat(personal.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
    }

    @Test
    public void givenBirthDate_whenSetBirthDate_thenGetBirthDate() {
        personal.setBirthDate(DEFAULT_DATE);
        assertThat(personal.getBirthDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    public void givenBiologicalSex_whenSetBiologicalSex_thenGetBiologicalSex() {
        personal.setBiologicalSex(DEFAULT_BIOLOGICAL_SEX);
        assertThat(personal.getBiologicalSex()).isEqualTo(DEFAULT_BIOLOGICAL_SEX);
    }

    @Test
    public void givenPastDate_whenSetBirthDate_thenGetPastDate() {
        personal.setBirthDate(PAST_DATE);
        assertThat(personal.getBirthDate()).isEqualTo(PAST_DATE);
    }

    @Test
    public void givenAllFields_whenSettingAllFields_thenAllFieldsAreSet() {
        personal.setFirstName(DEFAULT_FIRST_NAME);
        personal.setLastName(DEFAULT_LAST_NAME);
        personal.setBirthDate(DEFAULT_DATE);
        personal.setBiologicalSex(DEFAULT_BIOLOGICAL_SEX);

        assertThat(personal.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(personal.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(personal.getBirthDate()).isEqualTo(DEFAULT_DATE);
        assertThat(personal.getBiologicalSex()).isEqualTo(DEFAULT_BIOLOGICAL_SEX);
    }
}