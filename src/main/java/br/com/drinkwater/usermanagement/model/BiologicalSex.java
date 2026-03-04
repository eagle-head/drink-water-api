package br.com.drinkwater.usermanagement.model;

import br.com.drinkwater.core.CodedEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

/**
 * Biological sex classification for user profiles. Each constant carries an integer code persisted
 * in the database and resolved via {@link #fromCode(int)}.
 *
 * <p>Supported values: {@link #MALE} (code 1), {@link #FEMALE} (code 2).
 */
@Schema(description = "Biological sex classification")
public enum BiologicalSex implements CodedEnum {
    MALE(1),
    FEMALE(2);

    private static final Map<Integer, BiologicalSex> LOOKUP =
            CodedEnum.buildLookupMap(BiologicalSex.class);

    private final int code;

    BiologicalSex(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    /**
     * Resolves a BiologicalSex from its integer code.
     *
     * @param code the database integer code
     * @return the corresponding BiologicalSex
     * @throws IllegalArgumentException if the code does not match any constant
     */
    public static BiologicalSex fromCode(int code) {
        BiologicalSex value = LOOKUP.get(code);
        if (value == null) {
            throw new IllegalArgumentException("Invalid BiologicalSex code: " + code);
        }
        return value;
    }
}
