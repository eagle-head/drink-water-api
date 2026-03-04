package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.dto.AlarmSettingsDTO;
import br.com.drinkwater.usermanagement.dto.AlarmSettingsResponseDTO;
import br.com.drinkwater.usermanagement.model.AlarmSettings;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * Maps between {@link AlarmSettings} entities and {@link AlarmSettingsDTO}/{@link
 * AlarmSettingsResponseDTO}. All public methods throw {@link NullPointerException} on null input.
 */
@Component
public final class AlarmSettingsMapper {

    private static final String DTO_REQUIRED = "AlarmSettingsDTO cannot be null";
    private static final String ENTITY_REQUIRED = "AlarmSettings entity cannot be null";

    /**
     * Converts an AlarmSettingsDTO to a new AlarmSettings entity (without database ID).
     *
     * @param dto the alarm settings data
     * @return a new AlarmSettings instance
     */
    public AlarmSettings toEntity(AlarmSettingsDTO dto) {
        Objects.requireNonNull(dto, DTO_REQUIRED);

        return new AlarmSettings(
                dto.goal(), dto.intervalMinutes(), dto.dailyStartTime(), dto.dailyEndTime());
    }

    /**
     * Converts an AlarmSettings entity to an AlarmSettingsResponseDTO.
     *
     * @param entity the persisted alarm settings entity
     * @return the response DTO
     */
    public AlarmSettingsResponseDTO toDto(AlarmSettings entity) {
        Objects.requireNonNull(entity, ENTITY_REQUIRED);

        return new AlarmSettingsResponseDTO(
                entity.getGoal(),
                entity.getIntervalMinutes(),
                entity.getDailyStartTime(),
                entity.getDailyEndTime());
    }

    /**
     * Applies updates from an AlarmSettingsDTO to an existing AlarmSettings entity, preserving the
     * database ID.
     *
     * @param entity the existing alarm settings entity
     * @param dto the updated alarm settings data
     * @return a new AlarmSettings entity with the updated fields
     */
    public AlarmSettings updateEntity(AlarmSettings entity, AlarmSettingsDTO dto) {
        Objects.requireNonNull(entity, ENTITY_REQUIRED);
        Objects.requireNonNull(dto, DTO_REQUIRED);

        return entity.withUpdatedFields(
                dto.goal(), dto.intervalMinutes(), dto.dailyStartTime(), dto.dailyEndTime());
    }
}
