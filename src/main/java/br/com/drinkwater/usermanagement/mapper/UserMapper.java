package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.dto.*;
import br.com.drinkwater.usermanagement.model.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapper {
    private final PersonalMapper personalMapper;
    private final PhysicalMapper physicalMapper;
    private final AlarmSettingsMapper alarmSettingsMapper;

    public UserMapper(PersonalMapper personalMapper, PhysicalMapper physicalMapper,
                      AlarmSettingsMapper alarmSettingsMapper) {
        this.personalMapper = personalMapper;
        this.physicalMapper = physicalMapper;
        this.alarmSettingsMapper = alarmSettingsMapper;
    }

    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setPublicId(UUID.randomUUID());
        this.updateUserFromDTO(user, dto);

        return user;
    }

    public ResponseUserDTO toDto(User entity) {
        if (entity == null) {
            return null;
        }

        return new ResponseUserDTO(
                entity.getPublicId(),
                entity.getEmail(),
                personalMapper.toDto(entity.getPersonal()),
                physicalMapper.toDto(entity.getPhysical()),
                alarmSettingsMapper.toDto(entity.getSettings())
        );
    }

    public void updateUserFromDTO(User user, UserDTO userDTO) {
        if (user == null || userDTO == null) {
            return;
        }

        // TODO: A webhook/listener needs to be implemented in Keycloak to sync with the Resource Server
//        user.setEmail(userDTO.email());

        Personal personal = personalMapper.toEntity(userDTO.personal());
        user.setPersonal(personal);

        Physical physical = physicalMapper.toEntity(userDTO.physical());
        user.setPhysical(physical);

        AlarmSettings alarmSettings = alarmSettingsMapper.toEntity(userDTO.settings());
        if (alarmSettings != null) {
            if (user.getSettings() != null) {
                alarmSettings.setId(user.getSettings().getId());
            }

            alarmSettings.setUser(user);
            user.setSettings(alarmSettings);
        }
    }
}