package br.com.drinkwater.usermanagement.dto;

import br.com.drinkwater.usermanagement.model.BiologicalSex;
import br.com.drinkwater.usermanagement.model.HeightUnit;
import br.com.drinkwater.usermanagement.model.WeightUnit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record UserCreateDTO(

        @Email
        @NotBlank
        String email,

        @NotBlank
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

        @NotNull
        @Min(45)
        BigDecimal weight,

        @NotNull
        WeightUnit weightUnit,

        @NotNull
        @Min(100)
        BigDecimal height,

        @NotNull
        HeightUnit heightUnit,

        @NotNull
        @Valid
        AlarmSettingsDTO alarmSettings
) {
}

