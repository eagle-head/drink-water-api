package br.com.drinkwater.usermanagement.constants.dto;

import br.com.drinkwater.usermanagement.dto.UserResponseDTO;

import java.util.UUID;

public final class UserResponseDtoTestData {

    private UserResponseDtoTestData() {
    }

    public static final UUID DEFAULT_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    public static final UserResponseDTO DEFAULT = new UserResponseDTO(
            DEFAULT_UUID,
            "john.doe@example.com",
            PersonalDtoTestData.DEFAULT,
            PhysicalDtoTestData.DEFAULT,
            AlarmSettingsResponseDtoTestData.DEFAULT
    );

    public static final UserResponseDTO WITH_SPECIAL_CHARS = new UserResponseDTO(
            DEFAULT_UUID,
            "joao.silva@example.com",
            PersonalDtoTestData.WITH_SPECIAL_CHARS,
            PhysicalDtoTestData.DEFAULT,
            AlarmSettingsResponseDtoTestData.DEFAULT
    );

    public static final UserResponseDTO WITH_MAX_VALUES = new UserResponseDTO(
            DEFAULT_UUID,
            "john.doe@example.com",
            PersonalDtoTestData.DEFAULT,
            PhysicalDtoTestData.WITH_MAX_VALUES,
            AlarmSettingsResponseDtoTestData.WITH_MAX_VALUES
    );

    public static final UserResponseDTO WITH_MIN_VALUES = new UserResponseDTO(
            DEFAULT_UUID,
            "john.doe@example.com",
            PersonalDtoTestData.DEFAULT,
            PhysicalDtoTestData.WITH_MIN_VALUES,
            AlarmSettingsResponseDtoTestData.WITH_MIN_VALUES
    );
}