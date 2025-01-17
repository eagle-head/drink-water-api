package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.dto.PersonalDTO;
import br.com.drinkwater.usermanagement.model.Personal;
import org.springframework.stereotype.Component;

@Component
public class PersonalMapper {

    public Personal toEntity(PersonalDTO dto) {
        if (dto == null) {
            return null;
        }

        Personal personal = new Personal();
        personal.setFirstName(dto.firstName());
        personal.setLastName(dto.lastName());
        personal.setBirthDate(dto.birthDate());
        personal.setBiologicalSex(dto.biologicalSex());

        return personal;
    }

    public PersonalDTO toDto(Personal entity) {
        if (entity == null) {
            return null;
        }

        return new PersonalDTO(
                entity.getFirstName(),
                entity.getLastName(),
                entity.getBirthDate(),
                entity.getBiologicalSex()
        );
    }
}
