package br.com.drinkwater.hydrationtracking.mapper;

import br.com.drinkwater.hydrationtracking.dto.WaterIntakeDTO;
import br.com.drinkwater.hydrationtracking.dto.ResponseWaterIntakeDTO;
import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import br.com.drinkwater.usermanagement.model.User;
import org.springframework.stereotype.Component;

@Component
public class WaterIntakeMapper {

    public WaterIntake toEntity(WaterIntakeDTO dto, User user) {
        var waterIntake = new WaterIntake();
        waterIntake.setDateTimeUTC(dto.dateTimeUTC());
        waterIntake.setVolume(dto.volume());
        waterIntake.setVolumeUnit(dto.volumeUnit());
        waterIntake.setUser(user);

        return waterIntake;
    }

    public ResponseWaterIntakeDTO toResponseDTO(WaterIntake entity) {
        return new ResponseWaterIntakeDTO(
                entity.getId(),
                entity.getDateTimeUTC(),
                entity.getVolume(),
                entity.getVolumeUnit()
        );
    }
}