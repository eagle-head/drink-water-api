package br.com.drinkwater.usermanagement.constants.dto;

import br.com.drinkwater.usermanagement.constants.primitive.DateTimeTestData;
import br.com.drinkwater.usermanagement.constants.primitive.enums.BiologicalSexTestData;
import br.com.drinkwater.usermanagement.dto.PersonalDTO;

public final class PersonalDtoTestData {

    private PersonalDtoTestData() {
    }

    // Valid data
    public static final PersonalDTO DEFAULT = new PersonalDTO(
            "John",
            "Doe",
            DateTimeTestData.PAST_DATE,
            BiologicalSexTestData.DEFAULT
    );

    public static final PersonalDTO WITH_SPECIAL_CHARS = new PersonalDTO(
            "João",
            "da Silva",
            DateTimeTestData.PAST_DATE,
            BiologicalSexTestData.DEFAULT
    );

    // Invalid data
    public static final PersonalDTO WITH_NULL_FIRST_NAME = new PersonalDTO(
            null,
            "Doe",
            DateTimeTestData.PAST_DATE,
            BiologicalSexTestData.DEFAULT
    );

    public static final PersonalDTO WITH_NULL_LAST_NAME = new PersonalDTO(
            "John",
            null,
            DateTimeTestData.PAST_DATE,
            BiologicalSexTestData.DEFAULT
    );

    public static final PersonalDTO WITH_NULL_BIRTH_DATE = new PersonalDTO(
            "John",
            "Doe",
            null,
            BiologicalSexTestData.DEFAULT
    );

    public static final PersonalDTO WITH_NULL_BIOLOGICAL_SEX = new PersonalDTO(
            "John",
            "Doe",
            DateTimeTestData.PAST_DATE,
            null
    );

    public static final PersonalDTO WITH_SHORT_FIRST_NAME = new PersonalDTO(
            "J",
            "Doe",
            DateTimeTestData.PAST_DATE,
            BiologicalSexTestData.DEFAULT
    );

    public static final PersonalDTO WITH_SHORT_LAST_NAME = new PersonalDTO(
            "John",
            "D",
            DateTimeTestData.PAST_DATE,
            BiologicalSexTestData.DEFAULT
    );

    public static final PersonalDTO WITH_INVALID_CHARS_FIRST_NAME = new PersonalDTO(
            "John123",
            "Doe",
            DateTimeTestData.PAST_DATE,
            BiologicalSexTestData.DEFAULT
    );

    public static final PersonalDTO WITH_FUTURE_BIRTH_DATE = new PersonalDTO(
            "John",
            "Doe",
            DateTimeTestData.FUTURE_DATE,
            BiologicalSexTestData.DEFAULT
    );
}