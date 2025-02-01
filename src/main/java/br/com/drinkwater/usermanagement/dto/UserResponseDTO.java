package br.com.drinkwater.usermanagement.dto;

import java.util.UUID;

public record UserResponseDTO(

        UUID publicId,
        String email,
        PersonalDTO personal,
        PhysicalDTO physical,
        AlarmSettingsResponseDTO settings
) {
}