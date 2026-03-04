package br.com.drinkwater.usermanagement.model;

import br.com.drinkwater.core.CodedEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

/**
 * Height measurement units for user physical profiles. Each constant carries an integer code
 * persisted in the database and resolved via {@link #fromCode(int)}.
 *
 * <p>Currently supported: {@link #CM} (centimeters, code 1).
 */
@Schema(description = "Height measurement unit")
public enum HeightUnit implements CodedEnum {
    CM(1);

    private static final Map<Integer, HeightUnit> LOOKUP =
            CodedEnum.buildLookupMap(HeightUnit.class);

    private final int code;

    HeightUnit(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    /**
     * Resolves a HeightUnit from its integer code.
     *
     * @param code the database integer code
     * @return the corresponding HeightUnit
     * @throws IllegalArgumentException if the code does not match any constant
     */
    public static HeightUnit fromCode(int code) {
        HeightUnit value = LOOKUP.get(code);
        if (value == null) {
            throw new IllegalArgumentException("Invalid HeightUnit code: " + code);
        }
        return value;
    }
}
