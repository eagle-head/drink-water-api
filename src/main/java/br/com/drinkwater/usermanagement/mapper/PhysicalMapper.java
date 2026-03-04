package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.dto.PhysicalDTO;
import br.com.drinkwater.usermanagement.model.Physical;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * Maps between {@link Physical} value objects and {@link PhysicalDTO}. All public methods throw
 * {@link NullPointerException} on null input.
 */
@Component
public final class PhysicalMapper {

    private static final String DTO_REQUIRED = "Physical DTO cannot be null";
    private static final String ENTITY_REQUIRED = "Physical entity cannot be null";

    /**
     * Converts a PhysicalDTO to a Physical value object.
     *
     * @param dto the physical data
     * @return a new Physical instance
     */
    public Physical toEntity(PhysicalDTO dto) {
        Objects.requireNonNull(dto, DTO_REQUIRED);

        return new Physical(dto.weight(), dto.weightUnit(), dto.height(), dto.heightUnit());
    }

    /**
     * Converts a Physical value object to a PhysicalDTO.
     *
     * @param entity the physical data
     * @return a new PhysicalDTO instance
     */
    public PhysicalDTO toDto(Physical entity) {
        Objects.requireNonNull(entity, ENTITY_REQUIRED);

        return new PhysicalDTO(
                entity.getWeight(),
                entity.getWeightUnit(),
                entity.getHeight(),
                entity.getHeightUnit());
    }
}
