package br.com.drinkwater.usermanagement.model;

import static br.com.drinkwater.usermanagement.constants.PhysicalTestConstants.PHYSICAL;
import static br.com.drinkwater.usermanagement.constants.PhysicalTestConstants.PHYSICAL_DTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

final class PhysicalTest {

    private static final BigDecimal NEW_WEIGHT = BigDecimal.valueOf(80);
    private static final BigDecimal NEW_HEIGHT = BigDecimal.valueOf(180);

    @Test
    void givenValidParameters_whenCreatingWithConstructor_thenShouldNotBeNull() {
        // When & Then
        assertThat(PHYSICAL).isNotNull();
        assertThat(PHYSICAL.getWeight()).isEqualTo(PHYSICAL_DTO.weight());
        assertThat(PHYSICAL.getWeightUnit()).isEqualTo(PHYSICAL_DTO.weightUnit());
        assertThat(PHYSICAL.getHeight()).isEqualTo(PHYSICAL_DTO.height());
        assertThat(PHYSICAL.getHeightUnit()).isEqualTo(PHYSICAL_DTO.heightUnit());
    }

    @Test
    void givenNullWeight_whenCreatingWithConstructor_thenShouldThrowException() {
        // When & Then
        assertThatThrownBy(
                        () ->
                                new Physical(
                                        null,
                                        PHYSICAL_DTO.weightUnit(),
                                        PHYSICAL_DTO.height(),
                                        PHYSICAL_DTO.heightUnit()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Weight cannot be null");
    }

    @Test
    void givenNullWeightUnit_whenCreatingWithConstructor_thenShouldThrowException() {
        // When & Then
        assertThatThrownBy(
                        () ->
                                new Physical(
                                        PHYSICAL_DTO.weight(),
                                        null,
                                        PHYSICAL_DTO.height(),
                                        PHYSICAL_DTO.heightUnit()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Weight unit cannot be null");
    }

    @Test
    void givenNullHeight_whenCreatingWithConstructor_thenShouldThrowException() {
        // When & Then
        assertThatThrownBy(
                        () ->
                                new Physical(
                                        PHYSICAL_DTO.weight(),
                                        PHYSICAL_DTO.weightUnit(),
                                        null,
                                        PHYSICAL_DTO.heightUnit()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Height cannot be null");
    }

    @Test
    void givenNullHeightUnit_whenCreatingWithConstructor_thenShouldThrowException() {
        // When & Then
        assertThatThrownBy(
                        () ->
                                new Physical(
                                        PHYSICAL_DTO.weight(),
                                        PHYSICAL_DTO.weightUnit(),
                                        PHYSICAL_DTO.height(),
                                        null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Height unit cannot be null");
    }

    @Test
    void givenSameInstance_whenEquals_thenShouldBeTrue() {
        assertThat(PHYSICAL.equals(PHYSICAL)).isTrue();
    }

    @Test
    void givenTwoEqualPhysicalObjects_whenEquals_thenShouldBeTrue() {
        // Given
        var physical2 =
                new Physical(
                        PHYSICAL_DTO.weight(),
                        PHYSICAL_DTO.weightUnit(),
                        PHYSICAL_DTO.height(),
                        PHYSICAL_DTO.heightUnit());

        // When & Then
        assertThat(PHYSICAL).isEqualTo(physical2);
        assertThat(PHYSICAL.hashCode()).isEqualTo(physical2.hashCode());
    }

    @Test
    void givenPhysicalAndNull_whenEquals_thenShouldReturnFalse() {
        assertThat(PHYSICAL.equals(null)).isFalse();
    }

    @Test
    void givenDifferentObjectType_whenEquals_thenShouldReturnFalse() {
        assertThat(PHYSICAL).isNotEqualTo("Not a Physical instance");
    }

    @Test
    void givenDifferentWeight_whenEquals_thenShouldReturnFalse() {
        var physical2 =
                new Physical(
                        NEW_WEIGHT,
                        PHYSICAL_DTO.weightUnit(),
                        PHYSICAL_DTO.height(),
                        PHYSICAL_DTO.heightUnit());
        assertThat(PHYSICAL.equals(physical2)).isFalse();
    }

    @Test
    void givenDifferentHeight_whenEquals_thenShouldReturnFalse() {
        var physical2 =
                new Physical(
                        PHYSICAL_DTO.weight(),
                        PHYSICAL_DTO.weightUnit(),
                        NEW_HEIGHT,
                        PHYSICAL_DTO.heightUnit());
        assertThat(PHYSICAL.equals(physical2)).isFalse();
    }

    @Test
    void givenDifferentWeightUnitCode_whenEquals_thenShouldReturnFalse() {
        var physical2 =
                new Physical(
                        PHYSICAL_DTO.weight(),
                        99,
                        PHYSICAL_DTO.height(),
                        PHYSICAL_DTO.heightUnit().getCode());
        assertThat(PHYSICAL).isNotEqualTo(physical2);
    }

    @Test
    void givenDifferentHeightUnitCode_whenEquals_thenShouldReturnFalse() {
        var physical2 =
                new Physical(
                        PHYSICAL_DTO.weight(),
                        PHYSICAL_DTO.weightUnit().getCode(),
                        PHYSICAL_DTO.height(),
                        99);
        assertThat(PHYSICAL).isNotEqualTo(physical2);
    }

    @Test
    void givenPhysical_whenToString_thenShouldContainAllFields() {
        assertThat(PHYSICAL.toString())
                .contains("weight=" + PHYSICAL_DTO.weight())
                .contains("weightUnit=" + PHYSICAL_DTO.weightUnit())
                .contains("height=" + PHYSICAL_DTO.height())
                .contains("heightUnit=" + PHYSICAL_DTO.heightUnit());
    }

    @Test
    void givenDifferentPhysicalObjects_whenHashCode_thenShouldReturnDifferentValues() {
        var physical2 =
                new Physical(
                        NEW_WEIGHT,
                        PHYSICAL_DTO.weightUnit(),
                        PHYSICAL_DTO.height(),
                        PHYSICAL_DTO.heightUnit());
        assertThat(PHYSICAL.hashCode()).isNotEqualTo(physical2.hashCode());
    }
}
