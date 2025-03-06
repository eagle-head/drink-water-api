package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.dto.UserDTO;
import br.com.drinkwater.usermanagement.dto.UserResponseDTO;
import br.com.drinkwater.usermanagement.model.AlarmSettings;
import br.com.drinkwater.usermanagement.model.Personal;
import br.com.drinkwater.usermanagement.model.Physical;
import br.com.drinkwater.usermanagement.model.User;
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

    public User toEntity(UserDTO dto, UUID publicId) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setPublicId(publicId);
        this.updateUserFromDTO(user, dto);

        return user;
    }

    public UserResponseDTO toDto(User entity) {
        if (entity == null) {
            return null;
        }

        return new UserResponseDTO(
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

        user.setEmail(userDTO.email());

        Personal personal = personalMapper.toEntity(userDTO.personal());
        user.setPersonal(personal);

        Physical physical = physicalMapper.toEntity(userDTO.physical());
        user.setPhysical(physical);

        if (userDTO.settings() == null) {
            user.setSettings(null);
            return;
        }

        if (user.getSettings() != null) {
            this.alarmSettingsMapper.updateEntity(user.getSettings(), userDTO.settings());
            return;
        }

        AlarmSettings alarmSettings = this.alarmSettingsMapper.toEntity(userDTO.settings());
        alarmSettings.setUser(user);
        user.setSettings(alarmSettings);
    }
}