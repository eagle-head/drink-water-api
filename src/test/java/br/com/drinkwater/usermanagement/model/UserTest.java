package br.com.drinkwater.usermanagement.model;

import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static br.com.drinkwater.usermanagement.constants.UserManagementTestData.*;
import static org.assertj.core.api.Assertions.assertThat;

public final class UserTest {

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
    }

    @Test
    public void givenId_whenSetId_thenGetId() {
        user.setId(DEFAULT_ID);
        assertThat(user.getId()).isEqualTo(DEFAULT_ID);
    }

    @Test
    public void givenPublicId_whenSetPublicId_thenGetPublicId() {
        user.setPublicId(DEFAULT_UUID);
        assertThat(user.getPublicId()).isEqualTo(DEFAULT_UUID);
    }

    @Test
    public void givenEmail_whenSetEmail_thenGetEmail() {
        user.setEmail(DEFAULT_EMAIL);
        assertThat(user.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    public void givenPersonal_whenSetPersonal_thenGetPersonal() {
        user.setPersonal(DEFAULT_PERSONAL);
        assertThat(user.getPersonal()).isEqualTo(DEFAULT_PERSONAL);
    }

    @Test
    public void givenPhysical_whenSetPhysical_thenGetPhysical() {
        user.setPhysical(DEFAULT_PHYSICAL);
        assertThat(user.getPhysical()).isEqualTo(DEFAULT_PHYSICAL);
    }

    @Test
    public void givenSettings_whenSetSettings_thenGetSettings() {
        user.setSettings(DEFAULT_ALARM_SETTINGS);
        assertThat(user.getSettings()).isEqualTo(DEFAULT_ALARM_SETTINGS);
    }

    @Test
    public void givenNewUser_whenGetWaterIntakes_thenReturnNonNullSet() {
        assertThat(user.getWaterIntakes())
                .isNotNull()
                .isInstanceOf(HashSet.class)
                .isEmpty();
    }

    @Test
    public void givenWaterIntakes_whenSetWaterIntakes_thenGetWaterIntakes() {
        Set<WaterIntake> waterIntakes = new HashSet<>();
        WaterIntake intake = new WaterIntake();
        waterIntakes.add(intake);

        user.setWaterIntakes(waterIntakes);

        assertThat(user.getWaterIntakes())
                .isNotNull()
                .isInstanceOf(HashSet.class)
                .isEqualTo(waterIntakes)
                .hasSize(1)
                .contains(intake);
    }

    @Test
    public void givenAllFields_whenSettingAllFields_thenAllFieldsAreSet() {
        user.setId(DEFAULT_ID);
        user.setPublicId(DEFAULT_UUID);
        user.setEmail(DEFAULT_EMAIL);
        user.setPersonal(DEFAULT_PERSONAL);
        user.setPhysical(DEFAULT_PHYSICAL);
        user.setSettings(DEFAULT_ALARM_SETTINGS);
        Set<WaterIntake> waterIntakes = new HashSet<>();
        user.setWaterIntakes(waterIntakes);

        assertThat(user.getId()).isEqualTo(DEFAULT_ID);
        assertThat(user.getPublicId()).isEqualTo(DEFAULT_UUID);
        assertThat(user.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(user.getPersonal()).isEqualTo(DEFAULT_PERSONAL);
        assertThat(user.getPhysical()).isEqualTo(DEFAULT_PHYSICAL);
        assertThat(user.getSettings()).isEqualTo(DEFAULT_ALARM_SETTINGS);
        assertThat(user.getWaterIntakes())
                .isNotNull()
                .isInstanceOf(HashSet.class)
                .isEqualTo(waterIntakes);
    }
}