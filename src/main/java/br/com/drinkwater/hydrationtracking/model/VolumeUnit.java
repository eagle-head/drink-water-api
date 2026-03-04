package br.com.drinkwater.hydrationtracking.model;

import br.com.drinkwater.core.CodedEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

/**
 * Volume measurement units for water intake records. Each constant carries an integer code
 * persisted in the database and resolved via {@link #fromCode(int)}.
 *
 * <p>Currently supported: {@link #ML} (milliliters, code 1).
 */
@Schema(description = "Volume measurement unit")
public enum VolumeUnit implements CodedEnum {
    ML(1);

    private static final Map<Integer, VolumeUnit> LOOKUP =
            CodedEnum.buildLookupMap(VolumeUnit.class);

    private final int code;

    VolumeUnit(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    /**
     * Resolves a VolumeUnit from its integer code.
     *
     * @param code the database integer code
     * @return the corresponding VolumeUnit
     * @throws IllegalArgumentException if the code does not match any constant
     */
    public static VolumeUnit fromCode(int code) {
        VolumeUnit value = LOOKUP.get(code);
        if (value == null) {
            throw new IllegalArgumentException("Invalid VolumeUnit code: " + code);
        }
        return value;
    }
}
