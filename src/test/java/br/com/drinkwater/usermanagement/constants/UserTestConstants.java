package br.com.drinkwater.usermanagement.constants;

import br.com.drinkwater.usermanagement.dto.*;
import br.com.drinkwater.usermanagement.model.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.UUID;

public final class UserTestConstants {

    private UserTestConstants() {}

    public static final UUID USER_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    public static final User USER;
    public static final UserDTO USER_DTO;
    public static final UserResponseDTO USER_RESPONSE_DTO;
    public static final OffsetDateTime START_TIME = OffsetDateTime.of(2024, 1, 1, 8, 0, 0, 0, ZoneOffset.UTC);
    public static final OffsetDateTime END_TIME = OffsetDateTime.of(2024, 1, 1, 22, 0, 0, 0, ZoneOffset.UTC);
    public static final OffsetDateTime BIRTH_DATE = OffsetDateTime.of(1990, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);

    static {
        USER_DTO = new UserDTO(
                "john.doe@example.com",
                new PersonalDTO(
                        "John",
                        "Doe",
                        BIRTH_DATE,
                        BiologicalSex.MALE
                ),
                new PhysicalDTO(
                        BigDecimal.valueOf(70.5),
                        WeightUnit.KG,
                        BigDecimal.valueOf(175.0),
                        HeightUnit.CM
                ),
                new AlarmSettingsDTO(
                        2000,
                        60,
                        START_TIME,
                        END_TIME
                )
        );

        USER = createUserFromDTO();
        USER.setId(1L);
        USER.setPublicId(USER_UUID);
        USER.setWaterIntakes(new HashSet<>());

        USER_RESPONSE_DTO = new UserResponseDTO(
                USER_UUID,
                USER_DTO.email(),
                USER_DTO.personal(),
                USER_DTO.physical(),
                new AlarmSettingsResponseDTO(
                        2000,
                        60,
                        START_TIME,
                        END_TIME
                )
        );
    }

    private static User createUserFromDTO() {
        Personal personal = new Personal();
        personal.setFirstName(USER_DTO.personal().firstName());
        personal.setLastName(USER_DTO.personal().lastName());
        personal.setBirthDate(USER_DTO.personal().birthDate());
        personal.setBiologicalSex(USER_DTO.personal().biologicalSex());

        Physical physical = new Physical();
        physical.setWeight(USER_DTO.physical().weight());
        physical.setWeightUnit(USER_DTO.physical().weightUnit());
        physical.setHeight(USER_DTO.physical().height());
        physical.setHeightUnit(USER_DTO.physical().heightUnit());

        AlarmSettings alarmSettings = new AlarmSettings();
        alarmSettings.setGoal(USER_DTO.settings().goal());
        alarmSettings.setIntervalMinutes(USER_DTO.settings().intervalMinutes());
        alarmSettings.setDailyStartTime(USER_DTO.settings().dailyStartTime());
        alarmSettings.setDailyEndTime(USER_DTO.settings().dailyEndTime());

        User user = new User();
        user.setEmail(USER_DTO.email());
        user.setPersonal(personal);
        user.setPhysical(physical);
        user.setSettings(alarmSettings);

        return user;
    }
}