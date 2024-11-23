package br.com.drinkwater.usermanagement.dto;

import br.com.drinkwater.usermanagement.model.BiologicalSex;
import br.com.drinkwater.usermanagement.model.HeightUnit;
import br.com.drinkwater.usermanagement.model.WeightUnit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record UserUpdateDTO(

        @Email
        @NotBlank
        String email,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @Past
        OffsetDateTime birthDate,

        BiologicalSex biologicalSex,

        @Min(45)
        BigDecimal weight,

        WeightUnit weightUnit,

        @Min(100)
        BigDecimal height,

        HeightUnit heightUnit,

        @Valid
        AlarmSettingsDTO alarmSettings

) {
}
