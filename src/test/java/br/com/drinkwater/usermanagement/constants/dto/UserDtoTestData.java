package br.com.drinkwater.usermanagement.constants.dto;

import br.com.drinkwater.usermanagement.dto.UserDTO;

public final class UserDtoTestData {

    private UserDtoTestData() {
    }

    // Valid data
    public static final UserDTO DEFAULT = new UserDTO(
            "john.doe@example.com",
            PersonalDtoTestData.DEFAULT,
            PhysicalDtoTestData.DEFAULT,
            AlarmSettingsDtoTestData.DEFAULT
    );

    // Invalid data
    public static final UserDTO WITH_NULL_EMAIL = new UserDTO(
            null,
            PersonalDtoTestData.DEFAULT,
            PhysicalDtoTestData.DEFAULT,
            AlarmSettingsDtoTestData.DEFAULT
    );

    public static final UserDTO WITH_INVALID_EMAIL = new UserDTO(
            "invalid-email",
            PersonalDtoTestData.DEFAULT,
            PhysicalDtoTestData.DEFAULT,
            AlarmSettingsDtoTestData.DEFAULT
    );

    public static final UserDTO WITH_NULL_PERSONAL = new UserDTO(
            "john.doe@example.com",
            null,
            PhysicalDtoTestData.DEFAULT,
            AlarmSettingsDtoTestData.DEFAULT
    );

    public static final UserDTO WITH_NULL_PHYSICAL = new UserDTO(
            "john.doe@example.com",
            PersonalDtoTestData.DEFAULT,
            null,
            AlarmSettingsDtoTestData.DEFAULT
    );

    public static final UserDTO WITH_NULL_SETTINGS = new UserDTO(
            "john.doe@example.com",
            PersonalDtoTestData.DEFAULT,
            PhysicalDtoTestData.DEFAULT,
            null
    );

    public static final UserDTO WITH_INVALID_PERSONAL = new UserDTO(
            "john.doe@example.com",
            PersonalDtoTestData.WITH_NULL_FIRST_NAME,
            PhysicalDtoTestData.DEFAULT,
            AlarmSettingsDtoTestData.DEFAULT
    );

    public static final UserDTO WITH_INVALID_PHYSICAL = new UserDTO(
            "john.doe@example.com",
            PersonalDtoTestData.DEFAULT,
            PhysicalDtoTestData.WITH_NULL_WEIGHT,
            AlarmSettingsDtoTestData.DEFAULT
    );

    public static final UserDTO WITH_INVALID_SETTINGS = new UserDTO(
            "john.doe@example.com",
            PersonalDtoTestData.DEFAULT,
            PhysicalDtoTestData.DEFAULT,
            AlarmSettingsDtoTestData.WITH_NULL_START_TIME
    );
}