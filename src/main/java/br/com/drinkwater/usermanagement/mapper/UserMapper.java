package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.dto.UserDTO;
import br.com.drinkwater.usermanagement.dto.UserResponseDTO;
import br.com.drinkwater.usermanagement.model.User;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Maps between {@link User} entities and {@link UserDTO}/{@link UserResponseDTO}. Delegates
 * embedded object mapping to {@link PersonalMapper}, {@link PhysicalMapper}, and {@link
 * AlarmSettingsMapper}. All public methods throw {@link NullPointerException} on null input.
 */
@Component
public final class UserMapper {

    private static final String DTO_REQUIRED = "UserDTO cannot be null";
    private static final String PUBLIC_ID_REQUIRED = "Public ID cannot be null";
    private static final String ENTITY_REQUIRED = "User entity cannot be null";

    private final PersonalMapper personalMapper;
    private final PhysicalMapper physicalMapper;
    private final AlarmSettingsMapper alarmSettingsMapper;

    public UserMapper(
            PersonalMapper personalMapper,
            PhysicalMapper physicalMapper,
            AlarmSettingsMapper alarmSettingsMapper) {
        this.personalMapper = personalMapper;
        this.physicalMapper = physicalMapper;
        this.alarmSettingsMapper = alarmSettingsMapper;
    }

    /**
     * Converts a UserDTO to a new User entity (without database ID).
     *
     * @param dto the user data
     * @param publicId the Keycloak public ID
     * @return a new User entity
     */
    public User toEntity(UserDTO dto, UUID publicId) {
        Objects.requireNonNull(dto, DTO_REQUIRED);
        Objects.requireNonNull(publicId, PUBLIC_ID_REQUIRED);

        var personal = this.personalMapper.toEntity(dto.personal());
        var physical = this.physicalMapper.toEntity(dto.physical());
        var alarmSettings = this.alarmSettingsMapper.toEntity(dto.settings());

        return new User(publicId, dto.email(), personal, physical, alarmSettings);
    }

    /**
     * Converts a User entity to a UserResponseDTO.
     *
     * @param entity the persisted user entity
     * @return the response DTO
     */
    public UserResponseDTO toDto(User entity) {
        Objects.requireNonNull(entity, ENTITY_REQUIRED);

        return new UserResponseDTO(
                entity.getPublicId(),
                entity.getEmail(),
                this.personalMapper.toDto(entity.getPersonal()),
                this.physicalMapper.toDto(entity.getPhysical()),
                this.alarmSettingsMapper.toDto(entity.getSettings()));
    }

    /**
     * Applies updates from a UserDTO to an existing User entity, preserving the database ID and
     * public ID.
     *
     * @param user the existing user entity
     * @param userDTO the updated user data
     * @return a new User entity with the updated fields and settings
     */
    public User updateUser(User user, UserDTO userDTO) {
        Objects.requireNonNull(user, ENTITY_REQUIRED);
        Objects.requireNonNull(userDTO, DTO_REQUIRED);

        var personal = this.personalMapper.toEntity(userDTO.personal());
        var physical = this.physicalMapper.toEntity(userDTO.physical());

        var updatedUser = user.withUpdatedFields(userDTO.email(), personal, physical);

        var updatedSettings =
                this.alarmSettingsMapper.updateEntity(user.getSettings(), userDTO.settings());

        return updatedUser.withSettings(updatedSettings);
    }
}
