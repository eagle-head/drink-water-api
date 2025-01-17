package br.com.drinkwater.usermanagement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserDTO(

        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email address")
        @Size(max = 255, message = "Email cannot exceed 255 characters")
        String email,

        @NotNull(message = "Personal data is required")
        @Valid
        PersonalDTO personal,

        @NotNull(message = "Physical data is required")
        @Valid
        PhysicalDTO physical,

        @NotNull(message = "Alarm settings is required")
        @Valid
        AlarmSettingsDTO settings
) {
}