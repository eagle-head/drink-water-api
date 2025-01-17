package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.dto.PhysicalDTO;
import br.com.drinkwater.usermanagement.model.Physical;
import org.springframework.stereotype.Component;

@Component
public class PhysicalMapper {

    public Physical toEntity(PhysicalDTO dto) {
        if (dto == null) {
            return null;
        }

        Physical physical = new Physical();
        physical.setWeight(dto.weight());
        physical.setWeightUnit(dto.weightUnit());
        physical.setHeight(dto.height());
        physical.setHeightUnit(dto.heightUnit());

        return physical;
    }

    public PhysicalDTO toDto(Physical entity) {
        if (entity == null) {
            return null;
        }

        return new PhysicalDTO(
                entity.getWeight(),
                entity.getWeightUnit(),
                entity.getHeight(),
                entity.getHeightUnit()
        );
    }
}