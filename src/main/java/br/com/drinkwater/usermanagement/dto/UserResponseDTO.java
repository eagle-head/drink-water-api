package br.com.drinkwater.usermanagement.dto;

import br.com.drinkwater.usermanagement.model.BiologicalSex;
import br.com.drinkwater.usermanagement.model.HeightUnit;
import br.com.drinkwater.usermanagement.model.WeightUnit;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponseDTO(

        UUID publicId,
        String email,
        String firstName,
        String lastName,
        OffsetDateTime birthDate,
        BiologicalSex biologicalSex,
        BigDecimal weight,
        WeightUnit weightUnit,
        BigDecimal height,
        HeightUnit heightUnit,
        AlarmSettingsResponseDTO alarmSettings
) {
}
