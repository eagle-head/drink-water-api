package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.dto.AlarmSettingsDTO;
import br.com.drinkwater.usermanagement.dto.AlarmSettingsResponseDTO;
import br.com.drinkwater.usermanagement.model.AlarmSettings;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AlarmSettingsMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AlarmSettings toEntity(AlarmSettingsDTO dto);

    AlarmSettingsResponseDTO toDTO(AlarmSettings alarmSettings);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void toEntity(AlarmSettingsDTO dto, @MappingTarget AlarmSettings entity);
}