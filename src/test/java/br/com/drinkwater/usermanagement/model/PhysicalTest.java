package br.com.drinkwater.usermanagement.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public final class PhysicalTest {

    private static final BigDecimal WEIGHT = BigDecimal.valueOf(70);
    private static final BigDecimal NEW_WEIGHT = BigDecimal.valueOf(80);
    private static final BigDecimal HEIGHT = BigDecimal.valueOf(175);
    private static final BigDecimal NEW_HEIGHT = BigDecimal.valueOf(180);
    private static final WeightUnit WEIGHT_UNIT = WeightUnit.KG;
    private static final HeightUnit HEIGHT_UNIT = HeightUnit.CM;

    @ParameterizedTest(name = "Testing setter/getter with value: {2}")
    @MethodSource("provideSetterGetterPairs")
    public <T> void testGetterSetter(final BiConsumer<Physical, T> setter,
                                     final Function<Physical, T> getter,
                                     final T expectedValue) {
        var physical = new Physical();
        setter.accept(physical, expectedValue);

        assertThat(getter.apply(physical)).isEqualTo(expectedValue);
    }

    public static Stream<Arguments> provideSetterGetterPairs() {
        return Stream.of(
                Arguments.of((BiConsumer<Physical, BigDecimal>) Physical::setWeight,
                        (Function<Physical, BigDecimal>) Physical::getWeight,
                        WEIGHT),
                Arguments.of((BiConsumer<Physical, WeightUnit>) Physical::setWeightUnit,
                        (Function<Physical, WeightUnit>) Physical::getWeightUnit,
                        WEIGHT_UNIT),
                Arguments.of((BiConsumer<Physical, BigDecimal>) Physical::setHeight,
                        (Function<Physical, BigDecimal>) Physical::getHeight,
                        HEIGHT),
                Arguments.of((BiConsumer<Physical, HeightUnit>) Physical::setHeightUnit,
                        (Function<Physical, HeightUnit>) Physical::getHeightUnit,
                        HEIGHT_UNIT)
        );
    }

    private Physical createDefaultPhysical() {
        var physical = new Physical();
        physical.setWeight(WEIGHT);
        physical.setWeightUnit(WEIGHT_UNIT);
        physical.setHeight(HEIGHT);
        physical.setHeightUnit(HEIGHT_UNIT);

        return physical;
    }

    @Test
    public void givenNewPhysical_whenInstantiated_thenShouldNotBeNull() {
        var physical = new Physical();

        assertThat(physical).isNotNull();
    }


    @Test
    public void givenSameInstance_whenEquals_thenShouldBeTrue() {
        var physical = createDefaultPhysical();

        assertThat(physical.equals(physical)).isTrue();
    }

    @Test
    public void givenTwoEqualPhysicalObjects_whenEquals_thenShouldBeTrue() {
        var physical1 = createDefaultPhysical();
        var physical2 = createDefaultPhysical();

        assertThat(physical1).isEqualTo(physical2);
        assertThat(physical1.hashCode()).isEqualTo(physical2.hashCode());
    }

    @Test
    public void givenPhysicalAndNull_whenEquals_thenShouldReturnFalse() {
        var physical = createDefaultPhysical();

        assertThat(physical.equals(null)).isFalse();
    }

    @Test
    public void givenDifferentObjectType_whenEquals_thenShouldReturnFalse() {
        var physical = createDefaultPhysical();
        var notAPhysical = "Not a Physical instance";

        assertThat(physical.equals(notAPhysical)).isFalse();
    }

    @Test
    public void givenDifferentWeight_whenEquals_thenShouldReturnFalse() {
        var physical1 = createDefaultPhysical();
        var physical2 = createDefaultPhysical();
        physical2.setWeight(NEW_WEIGHT);

        assertThat(physical1.equals(physical2)).isFalse();
    }

    @Test
    public void givenOnePhysicalWithNullWeight_whenEquals_thenShouldReturnFalse() {
        Physical physical1 = createDefaultPhysical();
        Physical physical2 = createDefaultPhysical();
        physical2.setWeight(null);

        assertThat(physical1.equals(physical2)).isFalse();
    }

    @Test
    public void givenBothPhysicalWithNullWeight_whenEquals_thenShouldReturnTrue() {
        var physical1 = createDefaultPhysical();
        var physical2 = createDefaultPhysical();
        physical1.setWeight(null);
        physical2.setWeight(null);

        assertThat(physical1.equals(physical2)).isTrue();
    }

    @Test
    public void givenDifferentHeight_whenEquals_thenShouldReturnFalse() {
        var physical1 = createDefaultPhysical();
        var physical2 = createDefaultPhysical();
        physical2.setHeight(NEW_HEIGHT);

        assertThat(physical1.equals(physical2)).isFalse();
    }

    @Test
    public void givenOnePhysicalWithNullHeight_whenEquals_thenShouldReturnFalse() {
        Physical physical1 = createDefaultPhysical();
        Physical physical2 = createDefaultPhysical();
        physical2.setHeight(null);
        assertThat(physical1.equals(physical2)).isFalse();
    }

    @Test
    public void givenBothPhysicalWithNullHeight_whenEquals_thenShouldReturnTrue() {
        Physical physical1 = createDefaultPhysical();
        Physical physical2 = createDefaultPhysical();
        physical1.setHeight(null);
        physical2.setHeight(null);
        assertThat(physical1.equals(physical2)).isTrue();
    }

    @Test
    public void givenOnePhysicalWithNullWeightUnit_whenEquals_thenShouldReturnFalse() {
        Physical physical1 = createDefaultPhysical();
        Physical physical2 = createDefaultPhysical();
        physical2.setWeightUnit(null);
        assertThat(physical1.equals(physical2)).isFalse();
    }

    @Test
    public void givenBothPhysicalWithNullWeightUnit_whenEquals_thenShouldReturnTrue() {
        Physical physical1 = createDefaultPhysical();
        Physical physical2 = createDefaultPhysical();
        physical1.setWeightUnit(null);
        physical2.setWeightUnit(null);
        assertThat(physical1.equals(physical2)).isTrue();
    }

    @Test
    public void givenOnePhysicalWithNullHeightUnit_whenEquals_thenShouldReturnFalse() {
        Physical physical1 = createDefaultPhysical();
        Physical physical2 = createDefaultPhysical();
        physical2.setHeightUnit(null);
        assertThat(physical1.equals(physical2)).isFalse();
    }

    @Test
    public void givenBothPhysicalWithNullHeightUnit_whenEquals_thenShouldReturnTrue() {
        Physical physical1 = createDefaultPhysical();
        Physical physical2 = createDefaultPhysical();
        physical1.setHeightUnit(null);
        physical2.setHeightUnit(null);
        assertThat(physical1.equals(physical2)).isTrue();
    }

    @Test
    public void givenPhysical_whenToString_thenShouldContainAllFields() {
        Physical physical = createDefaultPhysical();
        String toString = physical.toString();
        assertThat(toString)
                .contains("weight=" + WEIGHT)
                .contains("weightUnit=" + WEIGHT_UNIT)
                .contains("height=" + HEIGHT)
                .contains("heightUnit=" + HEIGHT_UNIT);
    }

    @Test
    public void givenPhysicalWithNullFields_whenToString_thenShouldHandleNull() {
        Physical physical = new Physical();
        physical.setWeight(null);
        physical.setWeightUnit(null);
        physical.setHeight(null);
        physical.setHeightUnit(null);
        String toString = physical.toString();

        assertThat(toString)
                .contains("weight=null")
                .contains("weightUnit=null")
                .contains("height=null")
                .contains("heightUnit=null");
    }

    @Test
    public void givenDifferentPhysicalObjects_whenHashCode_thenShouldReturnDifferentValues() {
        Physical physical1 = createDefaultPhysical();
        Physical physical2 = createDefaultPhysical();
        physical2.setWeight(NEW_WEIGHT);

        assertThat(physical1.hashCode()).isNotEqualTo(physical2.hashCode());
    }
}
