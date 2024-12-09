package br.com.drinkwater.usermanagement.dto;

import br.com.drinkwater.usermanagement.model.BiologicalSex;
import br.com.drinkwater.usermanagement.model.HeightUnit;
import br.com.drinkwater.usermanagement.model.WeightUnit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record UserUpdateDTO(

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @Past
        OffsetDateTime birthDate,

        BiologicalSex biologicalSex,

        @Min(45)
        BigDecimal weight,

        @NotNull
        WeightUnit weightUnit,

        @Min(100)
        BigDecimal height,

        @NotNull
        HeightUnit heightUnit,

        @Valid
        AlarmSettingsDTO alarmSettings

) {
}
