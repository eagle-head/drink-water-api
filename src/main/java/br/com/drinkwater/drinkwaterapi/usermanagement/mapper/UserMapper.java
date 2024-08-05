package br.com.drinkwater.drinkwaterapi.usermanagement.mapper;

import br.com.drinkwater.drinkwaterapi.usermanagement.dto.UserCreateDTO;
import br.com.drinkwater.drinkwaterapi.usermanagement.dto.UserResponseDTO;
import br.com.drinkwater.drinkwaterapi.usermanagement.dto.UserUpdateDTO;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponseDTO convertToDTO(User user);
    User convertToEntity(UserCreateDTO userCreateDTO);
    User convertToEntity(UserUpdateDTO userUpdateDTO);
}
