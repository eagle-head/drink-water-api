package br.com.drinkwater.usermanagement.constants;

import br.com.drinkwater.usermanagement.dto.*;
import br.com.drinkwater.usermanagement.model.*;

import java.util.HashSet;
import java.util.UUID;

import static br.com.drinkwater.usermanagement.constants.AlarmSettingsTestConstants.*;
import static br.com.drinkwater.usermanagement.constants.PersonalTestConstants.*;
import static br.com.drinkwater.usermanagement.constants.PhysicalTestConstants.*;

public final class UserTestConstants {

    private UserTestConstants() {}

    public static final UUID USER_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    public static final User USER;
    public static final UserDTO USER_DTO;
    public static final UserResponseDTO USER_RESPONSE_DTO;

    static {
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