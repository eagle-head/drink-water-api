package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.dto.PhysicalDTO;
import br.com.drinkwater.usermanagement.model.Physical;
import org.springframework.stereotype.Component;

@Component
public class PhysicalDataMapper {

    public Physical toEntity(PhysicalDTO dto) {
        if (dto == null) {
            return null;
        }

        var physicalData = new Physical();
        physicalData.setWeight(dto.weight());
        physicalData.setWeightUnit(dto.weightUnit());
        physicalData.setHeight(dto.height());
        physicalData.setHeightUnit(dto.heightUnit());

        return physicalData;
    }

    public PhysicalDTO toDTO(Physical entity) {
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