package br.com.drinkwater.usermanagement.model;

import br.com.drinkwater.core.CodedEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

/**
 * Weight measurement units for user physical profiles. Each constant carries an integer code
 * persisted in the database and resolved via {@link #fromCode(int)}.
 *
 * <p>Currently supported: {@link #KG} (kilograms, code 1).
 */
@Schema(description = "Weight measurement unit")
public enum WeightUnit implements CodedEnum {
    KG(1);

    private static final Map<Integer, WeightUnit> LOOKUP =
            CodedEnum.buildLookupMap(WeightUnit.class);

    private final int code;

    WeightUnit(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    /**
     * Resolves a WeightUnit from its integer code.
     *
     * @param code the database integer code
     * @return the corresponding WeightUnit
     * @throws IllegalArgumentException if the code does not match any constant
     */
    public static WeightUnit fromCode(int code) {
        WeightUnit value = LOOKUP.get(code);
        if (value == null) {
            throw new IllegalArgumentException("Invalid WeightUnit code: " + code);
        }
        return value;
    }
}
