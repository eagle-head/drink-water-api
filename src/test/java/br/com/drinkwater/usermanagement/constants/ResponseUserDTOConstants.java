package br.com.drinkwater.usermanagement.constants;

import br.com.drinkwater.usermanagement.dto.*;
import br.com.drinkwater.usermanagement.model.BiologicalSex;
import br.com.drinkwater.usermanagement.model.HeightUnit;
import br.com.drinkwater.usermanagement.model.WeightUnit;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static br.com.drinkwater.usermanagement.constants.UserDTOConstants.JOHN_DOE_DTO;

public final class ResponseUserDTOConstants {

    private ResponseUserDTOConstants() {
        throw new UnsupportedOperationException("ResponseUserDTOConstants is a utility class and cannot be instantiated.");
    }

    public static final ResponseUserDTO JOHN_DOE_RESPONSE_DTO = createJohnDoeResponse();

    private static ResponseUserDTO createJohnDoeResponse() {
        UUID publicId = JOHN_DOE_DTO.email().hashCode() != 0
                ? UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
                : UUID.randomUUID();

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

        // Definition of alarm settings response
        AlarmSettingsResponseDTO alarmSettingsResponseDTO = new AlarmSettingsResponseDTO(
                2000,
                60,
                OffsetDateTime.parse("2024-01-01T08:00:00Z"),
                OffsetDateTime.parse("2024-01-01T22:00:00Z")
        );

        return new ResponseUserDTO(
                publicId,
                "johndoe@test.com",
                personalDTO,
                physicalDTO,
                alarmSettingsResponseDTO
        );
    }
}
