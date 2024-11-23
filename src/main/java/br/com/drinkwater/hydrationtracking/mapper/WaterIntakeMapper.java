package br.com.drinkwater.hydrationtracking.mapper;

import br.com.drinkwater.hydrationtracking.dto.WaterIntakeCreateDTO;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeResponseDTO;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeUpdateDTO;
import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import br.com.drinkwater.usermanagement.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WaterIntakeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    WaterIntake toEntity(WaterIntakeCreateDTO dto, User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void toEntity(WaterIntakeUpdateDTO dto, @MappingTarget WaterIntake entity);

    WaterIntakeResponseDTO toDto(WaterIntake waterIntake);
}
