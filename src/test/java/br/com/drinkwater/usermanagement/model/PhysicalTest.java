package br.com.drinkwater.usermanagement.model;

import static br.com.drinkwater.usermanagement.constants.PhysicalTestConstants.PHYSICAL;
import static br.com.drinkwater.usermanagement.constants.PhysicalTestConstants.PHYSICAL_DTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public final class PhysicalTest {

    private static final BigDecimal NEW_WEIGHT = BigDecimal.valueOf(80);
    private static final BigDecimal NEW_HEIGHT = BigDecimal.valueOf(180);

    @ParameterizedTest(name = "Testing setter/getter with value: {2}")
    @MethodSource("provideSetterGetterPairs")
    public <T> void testGetterSetter(final BiConsumer<Physical, T> setter,
                                     final Function<Physical, T> getter,
                                     final T expectedValue) {
        // Criando nova instância para não afetar outros testes
        var physical = PHYSICAL;
        setter.accept(physical, expectedValue);

        assertThat(getter.apply(physical)).isEqualTo(expectedValue);
    }

    public static Stream<Arguments> provideSetterGetterPairs() {
        return Stream.of(
                Arguments.of((BiConsumer<Physical, BigDecimal>) Physical::setWeight,
                        (Function<Physical, BigDecimal>) Physical::getWeight,
                        PHYSICAL_DTO.weight()),
                Arguments.of((BiConsumer<Physical, WeightUnit>) Physical::setWeightUnit,
                        (Function<Physical, WeightUnit>) Physical::getWeightUnit,
                        PHYSICAL_DTO.weightUnit()),
                Arguments.of((BiConsumer<Physical, BigDecimal>) Physical::setHeight,
                        (Function<Physical, BigDecimal>) Physical::getHeight,
                        PHYSICAL_DTO.height()),
                Arguments.of((BiConsumer<Physical, HeightUnit>) Physical::setHeightUnit,
                        (Function<Physical, HeightUnit>) Physical::getHeightUnit,
                        PHYSICAL_DTO.heightUnit())
        );
    }

    @Test
    public void givenValidParameters_whenCreatingWithConstructor_thenShouldNotBeNull() {
        assertThat(PHYSICAL).isNotNull();
        assertThat(PHYSICAL.getWeight()).isEqualTo(PHYSICAL_DTO.weight());
        assertThat(PHYSICAL.getWeightUnit()).isEqualTo(PHYSICAL_DTO.weightUnit());
        assertThat(PHYSICAL.getHeight()).isEqualTo(PHYSICAL_DTO.height());
        assertThat(PHYSICAL.getHeightUnit()).isEqualTo(PHYSICAL_DTO.heightUnit());
    }

    @Test
    public void givenNullWeight_whenCreatingWithConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new Physical(
                null,
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit())
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Weight cannot be null");
    }

    @Test
    public void givenNullWeightUnit_whenCreatingWithConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new Physical(
                PHYSICAL_DTO.weight(),
                null,
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit())
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Weight unit cannot be null");
    }

    @Test
    public void givenNullHeight_whenCreatingWithConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                null,
                PHYSICAL_DTO.heightUnit())
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Height cannot be null");
    }

    @Test
    public void givenNullHeightUnit_whenCreatingWithConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                null)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Height unit cannot be null");
    }

    @Test
    public void givenSameInstance_whenEquals_thenShouldBeTrue() {
        assertThat(PHYSICAL.equals(PHYSICAL)).isTrue();
    }

    @Test
    public void givenTwoEqualPhysicalObjects_whenEquals_thenShouldBeTrue() {
        var physical2 = new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit()
        );

        assertThat(PHYSICAL).isEqualTo(physical2);
        assertThat(PHYSICAL.hashCode()).isEqualTo(physical2.hashCode());
    }

    @Test
    public void givenPhysicalAndNull_whenEquals_thenShouldReturnFalse() {
        assertThat(PHYSICAL.equals(null)).isFalse();
    }

    @Test
    public void givenDifferentObjectType_whenEquals_thenShouldReturnFalse() {
        var notAPhysical = "Not a Physical instance";

        assertThat(PHYSICAL.equals(notAPhysical)).isFalse();
    }

    @Test
    public void givenDifferentWeight_whenEquals_thenShouldReturnFalse() {
        var physical2 = new Physical(
                NEW_WEIGHT,
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit()
        );

        assertThat(PHYSICAL.equals(physical2)).isFalse();
    }

    @Test
    public void givenDifferentHeight_whenEquals_thenShouldReturnFalse() {
        var physical2 = new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                NEW_HEIGHT,
                PHYSICAL_DTO.heightUnit()
        );

        assertThat(PHYSICAL.equals(physical2)).isFalse();
    }

    // Testes para cobrir branches que faltam no método equals
    @Test
    public void givenPhysicalWithNonNullWeightAndPhysicalWithNullWeight_whenEquals_thenShouldReturnFalse() {
        var physical1 = new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit()
        );

        var physical2 = new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit()
        );
        physical2.setWeight(null);

        assertThat(physical1.equals(physical2)).isFalse();
    }

    @Test
    public void givenPhysicalWithNullWeightAndPhysicalWithNonNullWeight_whenEquals_thenShouldReturnFalse() {
        var physical1 = new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit()
        );
        physical1.setWeight(null);

        var physical2 = new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit()
        );

        assertThat(physical1.equals(physical2)).isFalse();
    }

    @Test
    public void givenPhysicalWithNonNullHeightAndPhysicalWithNullHeight_whenEquals_thenShouldReturnFalse() {
        var physical1 = new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit()
        );

        var physical2 = new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit()
        );
        physical2.setHeight(null);

        assertThat(physical1.equals(physical2)).isFalse();
    }

    @Test
    public void givenPhysicalWithNullHeightAndPhysicalWithNonNullHeight_whenEquals_thenShouldReturnFalse() {
        var physical1 = new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit()
        );
        physical1.setHeight(null);

        var physical2 = new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit()
        );

        assertThat(physical1.equals(physical2)).isFalse();
    }

    @Test
    public void givenPhysicalWithNullWeightUnitAndPhysicalWithNonNullWeightUnit_whenEquals_thenShouldReturnFalse() {
        var physical1 = new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit()
        );
        physical1.setWeightUnit(null);

        var physical2 = new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit()
        );

        assertThat(physical1.equals(physical2)).isFalse();
    }

    @Test
    public void givenPhysicalWithNonNullWeightUnitAndPhysicalWithNullWeightUnit_whenEquals_thenShouldReturnFalse() {
        var physical1 = new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit()
        );

        var physical2 = new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit()
        );
        physical2.setWeightUnit(null);

        assertThat(physical1.equals(physical2)).isFalse();
    }

    @Test
    public void givenPhysicalWithNullHeightUnitAndPhysicalWithNonNullHeightUnit_whenEquals_thenShouldReturnFalse() {
        var physical1 = new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit()
        );
        physical1.setHeightUnit(null);

        var physical2 = new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit()
        );

        assertThat(physical1.equals(physical2)).isFalse();
    }

    @Test
    public void givenPhysicalWithNonNullHeightUnitAndPhysicalWithNullHeightUnit_whenEquals_thenShouldReturnFalse() {
        var physical1 = new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit()
        );

        var physical2 = new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit()
        );
        physical2.setHeightUnit(null);

        assertThat(physical1.equals(physical2)).isFalse();
    }

    @Test
    public void givenPhysical_whenToString_thenShouldContainAllFields() {
        assertThat(PHYSICAL.toString())
                .contains("weight=" + PHYSICAL_DTO.weight())
                .contains("weightUnit=" + PHYSICAL_DTO.weightUnit())
                .contains("height=" + PHYSICAL_DTO.height())
                .contains("heightUnit=" + PHYSICAL_DTO.heightUnit());
    }

    @Test
    public void givenDifferentPhysicalObjects_whenHashCode_thenShouldReturnDifferentValues() {
        var physical2 = new Physical(
                NEW_WEIGHT,
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit()
        );

        assertThat(PHYSICAL.hashCode()).isNotEqualTo(physical2.hashCode());
    }
}