package br.com.drinkwater.drinkwaterapi.usermanagement.dto;

import br.com.drinkwater.drinkwaterapi.usermanagement.model.BiologicalSex;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.HeightUnit;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.WeightUnit;
import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;

public record UserCreateDTO(
        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 6, max = 20)
        String password,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotNull
        @Past
        OffsetDateTime birthDate,

        @NotNull
        BiologicalSex biologicalSex,

        @Min(45)
        double weight,

        @NotNull
        WeightUnit weightUnit,

        @Min(100)
        double height,

        @NotNull
        HeightUnit heightUnit
) {}
