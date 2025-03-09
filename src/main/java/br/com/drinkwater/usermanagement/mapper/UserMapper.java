package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.dto.UserDTO;
import br.com.drinkwater.usermanagement.dto.UserResponseDTO;
import br.com.drinkwater.usermanagement.exception.UserMapperIllegalArgumentException;
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
            throw new UserMapperIllegalArgumentException("UserDTO cannot be null");
        }

        if (publicId == null) {
            throw new UserMapperIllegalArgumentException("Public ID cannot be null");
        }

        var personal = this.personalMapper.toEntity(dto.personal());
        var physical = this.physicalMapper.toEntity(dto.physical());
        var alarmSettings = this.alarmSettingsMapper.toEntity(dto.settings());

        return new User(publicId, dto.email(), personal, physical, alarmSettings);
    }

    public UserResponseDTO toDto(User entity) {
        if (entity == null) {
            throw new UserMapperIllegalArgumentException("Entity cannot be null");
        }

        return new UserResponseDTO(
                entity.getPublicId(),
                entity.getEmail(),
                this.personalMapper.toDto(entity.getPersonal()),
                this.physicalMapper.toDto(entity.getPhysical()),
                this.alarmSettingsMapper.toDto(entity.getSettings())
        );
    }

    public void updateUser(User user, UserDTO userDTO) {
        if (user == null) {
            throw new UserMapperIllegalArgumentException("User entity cannot be null");
        }

        if (userDTO == null) {
            throw new UserMapperIllegalArgumentException("UserDTO cannot be null");
        }

        user.setEmail(userDTO.email());

        var personal = this.personalMapper.toEntity(userDTO.personal());
        user.setPersonal(personal);

        var physical = this.physicalMapper.toEntity(userDTO.physical());
        user.setPhysical(physical);

        // Since the User entity guarantees a non-null AlarmSettings, update it directly.
        this.alarmSettingsMapper.updateEntity(user.getSettings(), userDTO.settings());
    }
}