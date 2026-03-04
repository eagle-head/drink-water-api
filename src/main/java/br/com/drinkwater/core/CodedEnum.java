package br.com.drinkwater.core;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Contract for enums that are persisted as integer codes in the database. Provides a static helper
 * to build an immutable reverse-lookup map from code to enum constant.
 */
public interface CodedEnum {

    /** Returns the integer code persisted in the database. */
    int getCode();

    /**
     * Builds an immutable map from integer code to enum constant for the given enum class.
     *
     * @param enumClass the enum class implementing CodedEnum
     * @param <E> the enum type
     * @return an unmodifiable map of code to enum constant
     */
    static <E extends Enum<E> & CodedEnum> Map<Integer, E> buildLookupMap(Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .collect(Collectors.toUnmodifiableMap(CodedEnum::getCode, Function.identity()));
    }
}
