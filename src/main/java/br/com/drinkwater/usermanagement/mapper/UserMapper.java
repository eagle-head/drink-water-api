package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.dto.UserCreateDTO;
import br.com.drinkwater.usermanagement.dto.UserResponseDTO;
import br.com.drinkwater.usermanagement.dto.UserUpdateDTO;
import br.com.drinkwater.usermanagement.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AlarmSettingsMapper.class})
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "waterIntakes", ignore = true)
    @Mapping(target = "alarmSettings.user", ignore = true)
    User toEntity(UserCreateDTO dto);

    @AfterMapping
    default void setAlarmSettingsUser(@MappingTarget User user) {
        if (user.getAlarmSettings() != null) {
            user.getAlarmSettings().setUser(user);
        }
    }

    @Mapping(target = "alarmSettings", source = "alarmSettings")
    UserResponseDTO toDTO(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "waterIntakes", ignore = true)
    @Mapping(target = "alarmSettings.user", ignore = true)
    void toEntity(UserUpdateDTO dto, @MappingTarget User entity);
}
