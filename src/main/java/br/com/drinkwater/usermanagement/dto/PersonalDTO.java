package br.com.drinkwater.usermanagement.dto;

import br.com.drinkwater.usermanagement.model.BiologicalSex;
import br.com.drinkwater.core.validation.date.ValidBirthDate;
import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;

public record PersonalDTO(

        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        @Pattern(
                regexp = "^[a-zA-ZÀ-ÿ\u0080-\u024F'](?:[a-zA-ZÀ-ÿ\u0080-\u024F'\\s-]*[a-zA-ZÀ-ÿ\u0080-\u024F']){1,}$",
                message = "Name must contain at least 2 valid characters and only letters, hyphens, apostrophes " +
                        "and single spaces between words"
        )
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        @Pattern(
                regexp = "^[a-zA-ZÀ-ÿ\u0080-\u024F'](?:[a-zA-ZÀ-ÿ\u0080-\u024F'\\s-]*[a-zA-ZÀ-ÿ\u0080-\u024F']){1,}$",
                message = "Name must contain at least 2 valid characters and only letters, hyphens, apostrophes " +
                        "and single spaces between words"
        )
        String lastName,

        @NotNull(message = "Birth date is required")
        @ValidBirthDate
        OffsetDateTime birthDate,

        @NotNull(message = "Biological sex is required")
        BiologicalSex biologicalSex
) {
}
