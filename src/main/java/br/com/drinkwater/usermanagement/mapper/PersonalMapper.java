package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.dto.PersonalDTO;
import br.com.drinkwater.usermanagement.model.Personal;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * Maps between {@link Personal} value objects and {@link PersonalDTO}. All public methods throw
 * {@link NullPointerException} on null input.
 */
@Component
public final class PersonalMapper {

    private static final String DTO_REQUIRED = "Personal DTO cannot be null";
    private static final String ENTITY_REQUIRED = "Personal entity cannot be null";

    /**
     * Converts a PersonalDTO to a Personal value object.
     *
     * @param dto the personal data
     * @return a new Personal instance
     */
    public Personal toEntity(PersonalDTO dto) {
        Objects.requireNonNull(dto, DTO_REQUIRED);

        return new Personal(dto.firstName(), dto.lastName(), dto.birthDate(), dto.biologicalSex());
    }

    /**
     * Converts a Personal value object to a PersonalDTO.
     *
     * @param entity the personal data
     * @return a new PersonalDTO instance
     */
    public PersonalDTO toDto(Personal entity) {
        Objects.requireNonNull(entity, ENTITY_REQUIRED);

        return new PersonalDTO(
                entity.getFirstName(),
                entity.getLastName(),
                entity.getBirthDate(),
                entity.getBiologicalSex());
    }
}
