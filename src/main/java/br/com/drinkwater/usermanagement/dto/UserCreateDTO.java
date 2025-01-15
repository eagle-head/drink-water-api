package br.com.drinkwater.usermanagement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record UserCreateDTO(

        @NotNull @Valid PersonalDataDTO personalData,
        @NotNull @Valid PhysicalDataDTO physicalData,
        @NotNull @Valid AlarmSettingsDTO alarmSettings
) {
}
