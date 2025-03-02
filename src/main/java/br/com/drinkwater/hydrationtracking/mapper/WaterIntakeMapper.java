package br.com.drinkwater.hydrationtracking.mapper;

import br.com.drinkwater.hydrationtracking.dto.WaterIntakeDTO;
import br.com.drinkwater.hydrationtracking.dto.ResponseWaterIntakeDTO;
import br.com.drinkwater.hydrationtracking.exception.WaterIntakeMapperIllegalArgumentException;
import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import br.com.drinkwater.usermanagement.model.User;
import org.springframework.stereotype.Component;

@Component
public class WaterIntakeMapper {

    public WaterIntake toEntity(WaterIntakeDTO dto, User user, Long id) {

        if (dto == null) {
            throw new WaterIntakeMapperIllegalArgumentException("Water intake DTO cannot be null.");
        }

        if (user == null) {
            throw new WaterIntakeMapperIllegalArgumentException("User cannot be null.");
        }

        if (id != null && id <= 0) {
            throw new WaterIntakeMapperIllegalArgumentException("ID must be greater than zero.");
        }

        if (id != null) {
            return new WaterIntake(
                    id,
                    dto.dateTimeUTC(),
                    dto.volume(),
                    dto.volumeUnit(),
                    user
            );
        }

        return new WaterIntake(
                dto.dateTimeUTC(),
                dto.volume(),
                dto.volumeUnit(),
                user
        );
    }

    public WaterIntake toEntity(WaterIntakeDTO dto, User user) {
        return this.toEntity(dto, user, null);
    }

    public ResponseWaterIntakeDTO toResponseDTO(WaterIntake entity) {

        if (entity == null) {
            throw new WaterIntakeMapperIllegalArgumentException("Water intake entity cannot be null.");
        }

        return new ResponseWaterIntakeDTO(
                entity.getId(),
                entity.getDateTimeUTC(),
                entity.getVolume(),
                entity.getVolumeUnit()
        );
    }
}