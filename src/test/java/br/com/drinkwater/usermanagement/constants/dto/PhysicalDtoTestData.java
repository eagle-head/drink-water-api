package br.com.drinkwater.usermanagement.constants.dto;

import br.com.drinkwater.usermanagement.constants.primitive.enums.HeightUnitTestData;
import br.com.drinkwater.usermanagement.constants.primitive.enums.WeightUnitTestData;
import br.com.drinkwater.usermanagement.dto.PhysicalDTO;

import java.math.BigDecimal;

public final class PhysicalDtoTestData {

    private PhysicalDtoTestData() {
    }

    // Valid data
    public static final PhysicalDTO DEFAULT = new PhysicalDTO(
            BigDecimal.valueOf(70.0),
            WeightUnitTestData.DEFAULT,
            BigDecimal.valueOf(170.0),
            HeightUnitTestData.DEFAULT
    );

    public static final PhysicalDTO WITH_MAX_VALUES = new PhysicalDTO(
            BigDecimal.valueOf(500.0),
            WeightUnitTestData.DEFAULT,
            BigDecimal.valueOf(250.0),
            HeightUnitTestData.DEFAULT
    );

    public static final PhysicalDTO WITH_MIN_VALUES = new PhysicalDTO(
            BigDecimal.valueOf(20.0),
            WeightUnitTestData.DEFAULT,
            BigDecimal.valueOf(50.0),
            HeightUnitTestData.DEFAULT
    );

    // Invalid data
    public static final PhysicalDTO WITH_NULL_WEIGHT = new PhysicalDTO(
            null,
            WeightUnitTestData.DEFAULT,
            BigDecimal.valueOf(170.0),
            HeightUnitTestData.DEFAULT
    );

    public static final PhysicalDTO WITH_NULL_HEIGHT = new PhysicalDTO(
            BigDecimal.valueOf(70.0),
            WeightUnitTestData.DEFAULT,
            null,
            HeightUnitTestData.DEFAULT
    );

    public static final PhysicalDTO WITH_NULL_WEIGHT_UNIT = new PhysicalDTO(
            BigDecimal.valueOf(70.0),
            null,
            BigDecimal.valueOf(170.0),
            HeightUnitTestData.DEFAULT
    );

    public static final PhysicalDTO WITH_NULL_HEIGHT_UNIT = new PhysicalDTO(
            BigDecimal.valueOf(70.0),
            WeightUnitTestData.DEFAULT,
            BigDecimal.valueOf(170.0),
            null
    );

    public static final PhysicalDTO WITH_WEIGHT_ABOVE_MAX = new PhysicalDTO(
            BigDecimal.valueOf(500.1),
            WeightUnitTestData.DEFAULT,
            BigDecimal.valueOf(170.0),
            HeightUnitTestData.DEFAULT
    );

    public static final PhysicalDTO WITH_WEIGHT_BELOW_MIN = new PhysicalDTO(
            BigDecimal.valueOf(19.9),
            WeightUnitTestData.DEFAULT,
            BigDecimal.valueOf(170.0),
            HeightUnitTestData.DEFAULT
    );

    public static final PhysicalDTO WITH_HEIGHT_ABOVE_MAX = new PhysicalDTO(
            BigDecimal.valueOf(70.0),
            WeightUnitTestData.DEFAULT,
            BigDecimal.valueOf(250.1),
            HeightUnitTestData.DEFAULT
    );

    public static final PhysicalDTO WITH_HEIGHT_BELOW_MIN = new PhysicalDTO(
            BigDecimal.valueOf(70.0),
            WeightUnitTestData.DEFAULT,
            BigDecimal.valueOf(49.9),
            HeightUnitTestData.DEFAULT
    );
}