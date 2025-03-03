package br.com.drinkwater.usermanagement.constants;

import br.com.drinkwater.usermanagement.dto.PersonalDTO;
import br.com.drinkwater.usermanagement.model.BiologicalSex;
import br.com.drinkwater.usermanagement.model.Personal;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class PersonalTestConstants {

    private PersonalTestConstants() {
    }

    public static final OffsetDateTime BIRTH_DATE = OffsetDateTime
            .of(1990, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    public static final PersonalDTO PERSONAL_DTO;
    public static final Personal PERSONAL;

    // Invalid DTOs for testing
    public static final PersonalDTO PERSONAL_DTO_NULL_FIRST_NAME;
    public static final PersonalDTO PERSONAL_DTO_EMPTY_FIRST_NAME;
    public static final PersonalDTO PERSONAL_DTO_NULL_LAST_NAME;
    public static final PersonalDTO PERSONAL_DTO_EMPTY_LAST_NAME;
    public static final PersonalDTO PERSONAL_DTO_NULL_BIRTH_DATE;
    public static final PersonalDTO PERSONAL_DTO_NULL_BIOLOGICAL_SEX;

    static {
        PERSONAL_DTO = new PersonalDTO(
                "John",
                "Doe",
                BIRTH_DATE,
                BiologicalSex.MALE
        );

        PERSONAL = createPersonalFromDTO();

        // Initialize invalid DTOs for testing
        PERSONAL_DTO_NULL_FIRST_NAME = new PersonalDTO(
                null,
                "Doe",
                BIRTH_DATE,
                BiologicalSex.MALE
        );

        PERSONAL_DTO_EMPTY_FIRST_NAME = new PersonalDTO(
                "",
                "Doe",
                BIRTH_DATE,
                BiologicalSex.MALE
        );

        PERSONAL_DTO_NULL_LAST_NAME = new PersonalDTO(
                "John",
                null,
                BIRTH_DATE,
                BiologicalSex.MALE
        );

        PERSONAL_DTO_EMPTY_LAST_NAME = new PersonalDTO(
                "John",
                "",
                BIRTH_DATE,
                BiologicalSex.MALE
        );

        PERSONAL_DTO_NULL_BIRTH_DATE = new PersonalDTO(
                "John",
                "Doe",
                null,
                BiologicalSex.MALE
        );

        PERSONAL_DTO_NULL_BIOLOGICAL_SEX = new PersonalDTO(
                "John",
                "Doe",
                BIRTH_DATE,
                null
        );
    }

    private static Personal createPersonalFromDTO() {
        return new Personal(
                PERSONAL_DTO.firstName(),
                PERSONAL_DTO.lastName(),
                PERSONAL_DTO.birthDate(),
                PERSONAL_DTO.biologicalSex()
        );
    }
}