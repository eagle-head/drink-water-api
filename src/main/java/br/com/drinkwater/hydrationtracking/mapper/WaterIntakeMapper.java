package br.com.drinkwater.hydrationtracking.mapper;

import br.com.drinkwater.hydrationtracking.dto.WaterIntakeDTO;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeResponseDTO;
import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import java.util.Objects;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Maps between {@link WaterIntake} entities and {@link WaterIntakeDTO}/{@link
 * WaterIntakeResponseDTO}. All public methods throw {@link NullPointerException} on null input. The
 * {@link #toDto(WaterIntake)} method additionally requires the entity to be persisted (non-null
 * ID).
 */
@Component
public final class WaterIntakeMapper {

    private static final String DTO_REQUIRED = "Water intake DTO cannot be null";
    private static final String USER_ID_REQUIRED = "User ID cannot be null";
    private static final String ID_POSITIVE = "ID must be greater than zero";
    private static final String ENTITY_REQUIRED = "Water intake entity cannot be null";

    /**
     * Converts a WaterIntakeDTO to a WaterIntake entity. If {@code id} is provided, the entity is
     * treated as an existing record (for updates); otherwise, a new entity is created.
     *
     * @param dto the water intake data
     * @param userId the internal database user ID
     * @param id the existing record ID, or null for new records
     * @return a WaterIntake entity
     * @throws IllegalArgumentException if {@code id} is non-null and not positive
     */
    public WaterIntake toEntity(WaterIntakeDTO dto, Long userId, @Nullable Long id) {
        Objects.requireNonNull(dto, DTO_REQUIRED);
        Objects.requireNonNull(userId, USER_ID_REQUIRED);

        if (id != null && id <= 0) {
            throw new IllegalArgumentException(ID_POSITIVE);
        }

        if (id != null) {
            return new WaterIntake(id, dto.dateTimeUTC(), dto.volume(), dto.volumeUnit(), userId);
        }

        return new WaterIntake(dto.dateTimeUTC(), dto.volume(), dto.volumeUnit(), userId);
    }

    /**
     * Converts a WaterIntakeDTO to a new WaterIntake entity (without database ID).
     *
     * @param dto the water intake data
     * @param userId the internal database user ID
     * @return a new WaterIntake entity
     */
    public WaterIntake toEntity(WaterIntakeDTO dto, Long userId) {
        return this.toEntity(dto, userId, null);
    }

    /**
     * Converts a persisted WaterIntake entity to a WaterIntakeResponseDTO.
     *
     * @param entity the persisted water intake entity (must have a non-null ID)
     * @return the response DTO
     */
    public WaterIntakeResponseDTO toDto(WaterIntake entity) {
        Objects.requireNonNull(entity, ENTITY_REQUIRED);

        return new WaterIntakeResponseDTO(
                Objects.requireNonNull(entity.getId(), "Entity must be persisted to map to DTO"),
                entity.getDateTimeUTC(),
                entity.getVolume(),
                entity.getVolumeUnit());
    }
}
