package br.com.drinkwater.usermanagement.dto;

import br.com.drinkwater.usermanagement.model.BiologicalSex;
import br.com.drinkwater.usermanagement.validation.ValidBirthDate;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PersonalDTO(

        @NotBlank(message = "{personalDTO.firstName.notBlank}")
        @Size(min = 2, max = 50, message = "{personalDTO.firstName.size}")
        @Pattern(
                regexp = "^[a-zA-ZÀ-ÿ\u0080-\u024F'](?:[a-zA-ZÀ-ÿ\u0080-\u024F'\\s-]*[a-zA-ZÀ-ÿ\u0080-\u024F']){1,}$",
                message = "{personalDTO.firstName.pattern}"
        )
        String firstName,

        @NotBlank(message = "{personalDTO.lastName.notBlank}")
        @Size(min = 2, max = 50, message = "{personalDTO.lastName.size}")
        @Pattern(
                regexp = "^[a-zA-ZÀ-ÿ\u0080-\u024F'](?:[a-zA-ZÀ-ÿ\u0080-\u024F'\\s-]*[a-zA-ZÀ-ÿ\u0080-\u024F']){1,}$",
                message = "{personalDTO.lastName.pattern}"
        )
        String lastName,

        @NotNull(message = "{personalDTO.birthDate.notNull}")
        @ValidBirthDate
        LocalDate birthDate,

        @NotNull(message = "{personalDTO.biologicalSex.notNull}")
        BiologicalSex biologicalSex
) {
}
