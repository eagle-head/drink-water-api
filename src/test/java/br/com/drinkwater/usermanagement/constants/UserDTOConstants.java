package br.com.drinkwater.usermanagement.constants;

import br.com.drinkwater.usermanagement.dto.*;
import br.com.drinkwater.usermanagement.model.BiologicalSex;
import br.com.drinkwater.usermanagement.model.HeightUnit;
import br.com.drinkwater.usermanagement.model.WeightUnit;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class UserDTOConstants {

    private UserDTOConstants() {
        throw new UnsupportedOperationException("UserDTOConstants is a utility class and cannot be instantiated.");
    }

    public static final UserDTO JOHN_DOE_DTO = createJohnDoeDTO();

    private static UserDTO createJohnDoeDTO() {
        // Definition of personal data
        PersonalDTO personalDTO = new PersonalDTO(
            "John",
            "Doe",
            OffsetDateTime.parse("1990-01-01T00:00:00Z"),
            BiologicalSex.MALE
        );

        // Definition of physical data
        PhysicalDTO physicalDTO = new PhysicalDTO(
            BigDecimal.valueOf(75.0),
            WeightUnit.KG,
            BigDecimal.valueOf(180),
            HeightUnit.CM
        );

        // Definition of alarm settings
        AlarmSettingsDTO alarmSettingsDTO = new AlarmSettingsDTO(
            2000, 
            60, 
            OffsetDateTime.parse("2024-01-01T08:00:00Z"), 
            OffsetDateTime.parse("2024-01-01T22:00:00Z")
        );

        return new UserDTO(
            "johndoe@test.com",
            personalDTO,
            physicalDTO,
            alarmSettingsDTO
        );
    }
}
