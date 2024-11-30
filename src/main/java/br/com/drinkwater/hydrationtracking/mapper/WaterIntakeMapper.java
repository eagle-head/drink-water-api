package br.com.drinkwater.hydrationtracking.mapper;

import br.com.drinkwater.hydrationtracking.dto.*;
import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import br.com.drinkwater.usermanagement.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

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

    default PaginatedWaterIntakeResponseDTO toPaginatedDto(Page<WaterIntake> waterIntakePage) {
        List<WaterIntakeResponseDTO> data = waterIntakePage.getContent()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        PaginationDTO pagination = new PaginationDTO(
                waterIntakePage.getNumber(),
                waterIntakePage.getSize(),
                waterIntakePage.getTotalElements(),
                waterIntakePage.getTotalPages(),
                waterIntakePage.isFirst(),
                waterIntakePage.isLast()
        );

        return new PaginatedWaterIntakeResponseDTO(data, pagination);
    }
}
