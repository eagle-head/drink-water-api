package br.com.drinkwater.usermanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.drinkwater.usermanagement.constants.UserManagementTestData.*;
import static org.assertj.core.api.Assertions.assertThat;

public final class PhysicalTest {

    private Physical physical;

    @BeforeEach
    public void setUp() {
        physical = new Physical();
    }

    @Test
    public void givenWeight_whenSetWeight_thenGetWeight() {
        physical.setWeight(DEFAULT_WEIGHT);
        assertThat(physical.getWeight()).isEqualTo(DEFAULT_WEIGHT);
    }

    @Test
    public void givenWeightUnit_whenSetWeightUnit_thenGetWeightUnit() {
        physical.setWeightUnit(DEFAULT_WEIGHT_UNIT);
        assertThat(physical.getWeightUnit()).isEqualTo(DEFAULT_WEIGHT_UNIT);
    }

    @Test
    public void givenHeight_whenSetHeight_thenGetHeight() {
        physical.setHeight(DEFAULT_HEIGHT);
        assertThat(physical.getHeight()).isEqualTo(DEFAULT_HEIGHT);
    }

    @Test
    public void givenHeightUnit_whenSetHeightUnit_thenGetHeightUnit() {
        physical.setHeightUnit(DEFAULT_HEIGHT_UNIT);
        assertThat(physical.getHeightUnit()).isEqualTo(DEFAULT_HEIGHT_UNIT);
    }

    @Test
    public void givenAllFields_whenSettingAllFields_thenAllFieldsAreSet() {
        physical.setWeight(DEFAULT_WEIGHT);
        physical.setWeightUnit(DEFAULT_WEIGHT_UNIT);
        physical.setHeight(DEFAULT_HEIGHT);
        physical.setHeightUnit(DEFAULT_HEIGHT_UNIT);

        assertThat(physical.getWeight()).isEqualTo(DEFAULT_WEIGHT);
        assertThat(physical.getWeightUnit()).isEqualTo(DEFAULT_WEIGHT_UNIT);
        assertThat(physical.getHeight()).isEqualTo(DEFAULT_HEIGHT);
        assertThat(physical.getHeightUnit()).isEqualTo(DEFAULT_HEIGHT_UNIT);
    }
}