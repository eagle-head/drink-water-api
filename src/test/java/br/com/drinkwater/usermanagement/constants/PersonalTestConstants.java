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

    static {
        PERSONAL_DTO = new PersonalDTO(
                "John",
                "Doe",
                BIRTH_DATE,
                BiologicalSex.MALE
        );

        PERSONAL = createPersonalFromDTO();
    }

    private static Personal createPersonalFromDTO() {
        var personal = new Personal();
        personal.setFirstName(PERSONAL_DTO.firstName());
        personal.setLastName(PERSONAL_DTO.lastName());
        personal.setBirthDate(PERSONAL_DTO.birthDate());
        personal.setBiologicalSex(PERSONAL_DTO.biologicalSex());

        return personal;
    }
}