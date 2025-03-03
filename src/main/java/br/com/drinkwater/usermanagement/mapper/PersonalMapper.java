package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.dto.PersonalDTO;
import br.com.drinkwater.usermanagement.exception.PersonalMapperIllegalArgumentException;
import br.com.drinkwater.usermanagement.model.Personal;
import org.springframework.stereotype.Component;

@Component
public class PersonalMapper {

    public Personal toEntity(PersonalDTO dto) {
        if (dto == null) {
            throw new PersonalMapperIllegalArgumentException("Personal DTO cannot be null.");
        }

        return new Personal(
                dto.firstName(),
                dto.lastName(),
                dto.birthDate(),
                dto.biologicalSex()
        );
    }

    public PersonalDTO toDto(Personal entity) {
        if (entity == null) {
            throw new PersonalMapperIllegalArgumentException("Personal entity cannot be null.");
        }

        return new PersonalDTO(
                entity.getFirstName(),
                entity.getLastName(),
                entity.getBirthDate(),
                entity.getBiologicalSex()
        );
    }
}