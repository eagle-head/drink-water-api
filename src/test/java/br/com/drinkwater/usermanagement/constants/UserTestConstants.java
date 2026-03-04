package br.com.drinkwater.usermanagement.constants;

import static br.com.drinkwater.usermanagement.constants.AlarmSettingsTestConstants.*;
import static br.com.drinkwater.usermanagement.constants.PersonalTestConstants.*;
import static br.com.drinkwater.usermanagement.constants.PhysicalTestConstants.*;

import br.com.drinkwater.usermanagement.dto.*;
import br.com.drinkwater.usermanagement.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public final class UserTestConstants {

    private UserTestConstants() {}

    // Constants for regular tests
    public static final Long USER_ID = 1L;
    public static final UUID USER_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    public static final User USER;
    public static final UserDTO USER_DTO;
    public static final UserResponseDTO USER_RESPONSE_DTO;

    // Constants for update tests
    public static final String UPDATE_EMAIL = "john.update@example.com";
    public static final OffsetDateTime UPDATE_NOW =
            OffsetDateTime.of(2024, 6, 15, 10, 0, 0, 0, ZoneOffset.UTC);

    public static final PersonalDTO UPDATE_PERSONAL_DTO =
            new PersonalDTO("John", "Update", LocalDate.of(1999, 6, 15), BiologicalSex.MALE);

    public static final PhysicalDTO UPDATE_PHYSICAL_DTO =
            new PhysicalDTO(
                    BigDecimal.valueOf(70.5),
                    WeightUnit.KG,
                    BigDecimal.valueOf(175),
                    HeightUnit.CM);

    public static final AlarmSettingsDTO UPDATE_SETTINGS_DTO =
            new AlarmSettingsDTO(2000, 30, LocalTime.of(8, 0), LocalTime.of(22, 0));

    public static final UserDTO UPDATE_USER_DTO =
            new UserDTO(
                    UPDATE_EMAIL, UPDATE_PERSONAL_DTO, UPDATE_PHYSICAL_DTO, UPDATE_SETTINGS_DTO);

    // Constants for alternative scenarios
    public static final UserDTO UPDATE_USER_DTO_WITHOUT_SETTINGS =
            new UserDTO(UPDATE_EMAIL, UPDATE_PERSONAL_DTO, UPDATE_PHYSICAL_DTO, null);

    public static final AlarmSettings EXISTING_ALARM_SETTINGS;

    public static final LocalTime INVALID_EARLY_START_TIME = LocalTime.of(5, 0);
    public static final LocalTime INVALID_LATE_END_TIME = LocalTime.of(23, 0);

    public static final AlarmSettingsDTO INVALID_EARLY_TIME_SETTINGS_DTO =
            new AlarmSettingsDTO(2000, 30, INVALID_EARLY_START_TIME, LocalTime.of(17, 0));

    public static final AlarmSettingsDTO INVALID_LATE_TIME_SETTINGS_DTO =
            new AlarmSettingsDTO(2000, 30, LocalTime.of(8, 0), INVALID_LATE_END_TIME);

    public static final UserDTO INVALID_EARLY_TIME_USER_DTO =
            new UserDTO(
                    UPDATE_EMAIL,
                    UPDATE_PERSONAL_DTO,
                    UPDATE_PHYSICAL_DTO,
                    INVALID_EARLY_TIME_SETTINGS_DTO);

    public static final UserDTO INVALID_LATE_TIME_USER_DTO =
            new UserDTO(
                    UPDATE_EMAIL,
                    UPDATE_PERSONAL_DTO,
                    UPDATE_PHYSICAL_DTO,
                    INVALID_LATE_TIME_SETTINGS_DTO);

    static {
        USER_DTO =
                new UserDTO("john.doe@example.com", PERSONAL_DTO, PHYSICAL_DTO, ALARM_SETTINGS_DTO);

        USER = new User(USER_ID, USER_UUID, USER_DTO.email(), PERSONAL, PHYSICAL, ALARM_SETTINGS);

        USER_RESPONSE_DTO =
                new UserResponseDTO(
                        USER_UUID,
                        USER_DTO.email(),
                        USER_DTO.personal(),
                        USER_DTO.physical(),
                        ALARM_SETTINGS_RESPONSE_DTO);

        EXISTING_ALARM_SETTINGS =
                new AlarmSettings(1000, 20, LocalTime.of(7, 0), LocalTime.of(21, 0));
    }
}
