package br.com.drinkwater.usermanagement.constants;

import br.com.drinkwater.usermanagement.dto.*;
import br.com.drinkwater.usermanagement.model.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.UUID;

import static br.com.drinkwater.usermanagement.constants.AlarmSettingsTestConstants.*;
import static br.com.drinkwater.usermanagement.constants.PersonalTestConstants.*;
import static br.com.drinkwater.usermanagement.constants.PhysicalTestConstants.*;

public final class UserTestConstants {

    private UserTestConstants() {}

    // Constants for regular tests
    public static final UUID USER_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    public static final User USER;
    public static final UserDTO USER_DTO;
    public static final UserResponseDTO USER_RESPONSE_DTO;

    // Constants for update tests
    public static final String UPDATE_EMAIL = "john.update@example.com";
    public static final OffsetDateTime UPDATE_NOW = OffsetDateTime.now();

    public static final PersonalDTO UPDATE_PERSONAL_DTO = new PersonalDTO(
            "John",
            "Update",
            UPDATE_NOW.minusYears(25),
            BiologicalSex.MALE
    );

    public static final PhysicalDTO UPDATE_PHYSICAL_DTO = new PhysicalDTO(
            BigDecimal.valueOf(70.5),
            WeightUnit.KG,
            BigDecimal.valueOf(175),
            HeightUnit.CM
    );

    public static final AlarmSettingsDTO UPDATE_SETTINGS_DTO = new AlarmSettingsDTO(
            2000,
            30,
            UPDATE_NOW.withHour(8).withMinute(0),
            UPDATE_NOW.withHour(22).withMinute(0)
    );

    public static final UserDTO UPDATE_USER_DTO = new UserDTO(
            UPDATE_EMAIL,
            UPDATE_PERSONAL_DTO,
            UPDATE_PHYSICAL_DTO,
            UPDATE_SETTINGS_DTO
    );

    // Constants for alternative scenarios
    public static final UserDTO UPDATE_USER_DTO_WITHOUT_SETTINGS = new UserDTO(
            UPDATE_EMAIL,
            UPDATE_PERSONAL_DTO,
            UPDATE_PHYSICAL_DTO,
            null
    );

    public static final AlarmSettings EXISTING_ALARM_SETTINGS;

    static {
        // Initialize regular test constants
        USER_DTO = new UserDTO(
                "john.doe@example.com",
                PERSONAL_DTO,
                PHYSICAL_DTO,
                ALARM_SETTINGS_DTO
        );

        USER = createUserFromDTO();
        USER.setPublicId(USER_UUID);
        USER.setWaterIntakes(new HashSet<>());

        USER_RESPONSE_DTO = new UserResponseDTO(
                USER_UUID,
                USER_DTO.email(),
                USER_DTO.personal(),
                USER_DTO.physical(),
                ALARM_SETTINGS_RESPONSE_DTO
        );

        // Initialize EXISTING_ALARM_SETTINGS
        EXISTING_ALARM_SETTINGS = new AlarmSettings();
        EXISTING_ALARM_SETTINGS.setGoal(1000);
        EXISTING_ALARM_SETTINGS.setIntervalMinutes(20);
        EXISTING_ALARM_SETTINGS.setDailyStartTime(UPDATE_NOW.withHour(7).withMinute(0));
        EXISTING_ALARM_SETTINGS.setDailyEndTime(UPDATE_NOW.withHour(21).withMinute(0));
    }

    private static User createUserFromDTO() {
        User user = new User();
        user.setEmail(USER_DTO.email());
        user.setPersonal(PERSONAL);
        user.setPhysical(PHYSICAL);
        user.setSettings(ALARM_SETTINGS);

        return user;
    }
}