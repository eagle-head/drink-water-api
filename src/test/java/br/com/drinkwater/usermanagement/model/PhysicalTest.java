package br.com.drinkwater.usermanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public final class PhysicalTest {

    private static final BigDecimal WEIGHT = BigDecimal.valueOf(70.5);
    private static final WeightUnit WEIGHT_UNIT = WeightUnit.KG;
    private static final BigDecimal HEIGHT = BigDecimal.valueOf(175.0);
    private static final HeightUnit HEIGHT_UNIT = HeightUnit.CM;

    private Physical physical;

    @BeforeEach
    public void setUp() {
        physical = new Physical();
    }

    @Test
    public void givenWeight_whenSetWeight_thenGetWeight() {
        physical.setWeight(WEIGHT);
        assertThat(physical.getWeight()).isEqualTo(WEIGHT);
    }

    @Test
    public void givenWeightUnit_whenSetWeightUnit_thenGetWeightUnit() {
        physical.setWeightUnit(WEIGHT_UNIT);
        assertThat(physical.getWeightUnit()).isEqualTo(WEIGHT_UNIT);
    }

    @Test
    public void givenHeight_whenSetHeight_thenGetHeight() {
        physical.setHeight(HEIGHT);
        assertThat(physical.getHeight()).isEqualTo(HEIGHT);
    }

    @Test
    public void givenHeightUnit_whenSetHeightUnit_thenGetHeightUnit() {
        physical.setHeightUnit(HEIGHT_UNIT);
        assertThat(physical.getHeightUnit()).isEqualTo(HEIGHT_UNIT);
    }

    @Test
    public void givenAllFields_whenSettingAllFields_thenAllFieldsAreSet() {
        physical.setWeight(WEIGHT);
        physical.setWeightUnit(WEIGHT_UNIT);
        physical.setHeight(HEIGHT);
        physical.setHeightUnit(HEIGHT_UNIT);

        assertThat(physical.getWeight()).isEqualTo(WEIGHT);
        assertThat(physical.getWeightUnit()).isEqualTo(WEIGHT_UNIT);
        assertThat(physical.getHeight()).isEqualTo(HEIGHT);
        assertThat(physical.getHeightUnit()).isEqualTo(HEIGHT_UNIT);
    }
}