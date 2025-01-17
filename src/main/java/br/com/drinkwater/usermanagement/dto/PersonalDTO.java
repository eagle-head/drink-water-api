package br.com.drinkwater.usermanagement.dto;

import br.com.drinkwater.usermanagement.model.BiologicalSex;
import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;

public record PersonalDTO(

        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s]*$", message = "First name must contain only letters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s]*$", message = "Last name must contain only letters")
        String lastName,

        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        OffsetDateTime birthDate,

        @NotNull(message = "Biological sex is required")
        BiologicalSex biologicalSex
) {
}
