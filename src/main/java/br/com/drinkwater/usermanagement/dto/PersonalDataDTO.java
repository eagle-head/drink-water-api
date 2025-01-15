package br.com.drinkwater.usermanagement.dto;

import br.com.drinkwater.usermanagement.model.BiologicalSex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.OffsetDateTime;

public record PersonalDataDTO(

        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull @Past OffsetDateTime birthDate,
        @NotNull BiologicalSex biologicalSex
) {
}