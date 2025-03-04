package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.dto.PhysicalDTO;
import br.com.drinkwater.usermanagement.exception.PhysicalMapperIllegalArgumentException;
import br.com.drinkwater.usermanagement.model.Physical;
import org.springframework.stereotype.Component;

@Component
public class PhysicalMapper {

    public Physical toEntity(PhysicalDTO dto) {
        if (dto == null) {
            throw new PhysicalMapperIllegalArgumentException("Physical DTO cannot be null.");
        }

        return new Physical(
                dto.weight(),
                dto.weightUnit(),
                dto.height(),
                dto.heightUnit()
        );
    }

    public PhysicalDTO toDto(Physical entity) {
        if (entity == null) {
            throw new PhysicalMapperIllegalArgumentException("Physical entity cannot be null.");
        }

        return new PhysicalDTO(
                entity.getWeight(),
                entity.getWeightUnit(),
                entity.getHeight(),
                entity.getHeightUnit()
        );
    }
}