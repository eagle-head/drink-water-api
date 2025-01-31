package br.com.drinkwater.usermanagement.constants.model.embedded;

import br.com.drinkwater.usermanagement.constants.primitive.enums.HeightUnitTestData;
import br.com.drinkwater.usermanagement.constants.primitive.enums.WeightUnitTestData;
import br.com.drinkwater.usermanagement.model.Physical;

import java.math.BigDecimal;

public final class PhysicalTestData {

    private PhysicalTestData() {
    }

    // Valid data
    public static final Physical DEFAULT = createDefault();
    public static final Physical WITH_MAX_VALUES = createWithMaxValues();
    public static final Physical WITH_MIN_VALUES = createWithMinValues();

    // Invalid data
    public static final Physical NULL = null;
    public static final Physical WITH_NULL_HEIGHT = createWithNullHeight();
    public static final Physical WITH_NULL_WEIGHT = createWithNullWeight();
    public static final Physical WITH_NULL_HEIGHT_UNIT = createWithNullHeightUnit();
    public static final Physical WITH_NULL_WEIGHT_UNIT = createWithNullWeightUnit();
    public static final Physical WITH_NEGATIVE_HEIGHT = createWithNegativeHeight();
    public static final Physical WITH_NEGATIVE_WEIGHT = createWithNegativeWeight();
    public static final Physical WITH_ZERO_HEIGHT = createWithZeroHeight();
    public static final Physical WITH_ZERO_WEIGHT = createWithZeroWeight();

    private static Physical createDefault() {
        Physical physical = new Physical();
        physical.setHeight(BigDecimal.valueOf(170.0));
        physical.setWeight(BigDecimal.valueOf(70.0));
        physical.setHeightUnit(HeightUnitTestData.DEFAULT);
        physical.setWeightUnit(WeightUnitTestData.DEFAULT);
        return physical;
    }

    private static Physical createWithMaxValues() {
        Physical physical = new Physical();
        physical.setHeight(BigDecimal.valueOf(250.0));
        physical.setWeight(BigDecimal.valueOf(200.0));
        physical.setHeightUnit(HeightUnitTestData.DEFAULT);
        physical.setWeightUnit(WeightUnitTestData.DEFAULT);
        return physical;
    }

    private static Physical createWithMinValues() {
        Physical physical = new Physical();
        physical.setHeight(BigDecimal.valueOf(50.0));
        physical.setWeight(BigDecimal.valueOf(30.0));
        physical.setHeightUnit(HeightUnitTestData.DEFAULT);
        physical.setWeightUnit(WeightUnitTestData.DEFAULT);
        return physical;
    }

    private static Physical createWithNullHeight() {
        Physical physical = new Physical();
        physical.setHeight(null);
        physical.setWeight(BigDecimal.valueOf(70.0));
        physical.setHeightUnit(HeightUnitTestData.DEFAULT);
        physical.setWeightUnit(WeightUnitTestData.DEFAULT);
        return physical;
    }

    private static Physical createWithNullWeight() {
        Physical physical = new Physical();
        physical.setHeight(BigDecimal.valueOf(170.0));
        physical.setWeight(null);
        physical.setHeightUnit(HeightUnitTestData.DEFAULT);
        physical.setWeightUnit(WeightUnitTestData.DEFAULT);
        return physical;
    }

    private static Physical createWithNullHeightUnit() {
        Physical physical = new Physical();
        physical.setHeight(BigDecimal.valueOf(170.0));
        physical.setWeight(BigDecimal.valueOf(70.0));
        physical.setHeightUnit(null);
        physical.setWeightUnit(WeightUnitTestData.DEFAULT);
        return physical;
    }

    private static Physical createWithNullWeightUnit() {
        Physical physical = new Physical();
        physical.setHeight(BigDecimal.valueOf(170.0));
        physical.setWeight(BigDecimal.valueOf(70.0));
        physical.setHeightUnit(HeightUnitTestData.DEFAULT);
        physical.setWeightUnit(null);
        return physical;
    }

    private static Physical createWithNegativeHeight() {
        Physical physical = new Physical();
        physical.setHeight(BigDecimal.valueOf(-170.0));
        physical.setWeight(BigDecimal.valueOf(70.0));
        physical.setHeightUnit(HeightUnitTestData.DEFAULT);
        physical.setWeightUnit(WeightUnitTestData.DEFAULT);
        return physical;
    }

    private static Physical createWithNegativeWeight() {
        Physical physical = new Physical();
        physical.setHeight(BigDecimal.valueOf(170.0));
        physical.setWeight(BigDecimal.valueOf(-70.0));
        physical.setHeightUnit(HeightUnitTestData.DEFAULT);
        physical.setWeightUnit(WeightUnitTestData.DEFAULT);
        return physical;
    }

    private static Physical createWithZeroHeight() {
        Physical physical = new Physical();
        physical.setHeight(BigDecimal.valueOf(0.0));
        physical.setWeight(BigDecimal.valueOf(70.0));
        physical.setHeightUnit(HeightUnitTestData.DEFAULT);
        physical.setWeightUnit(WeightUnitTestData.DEFAULT);
        return physical;
    }

    private static Physical createWithZeroWeight() {
        Physical physical = new Physical();
        physical.setHeight(BigDecimal.valueOf(170.0));
        physical.setWeight(BigDecimal.valueOf(0.0));
        physical.setHeightUnit(HeightUnitTestData.DEFAULT);
        physical.setWeightUnit(WeightUnitTestData.DEFAULT);
        return physical;
    }
}