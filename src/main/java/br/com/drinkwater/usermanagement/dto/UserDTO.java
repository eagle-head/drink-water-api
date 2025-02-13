package br.com.drinkwater.usermanagement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record UserDTO(

        @NotBlank(message = "{userDTO.email.notBlank}")
        @Email(message = "{userDTO.email.email}")
        @Pattern(
                regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "{userDTO.email.pattern}"
        )
        @Size(max = 255, message = "{userDTO.email.size}")
        String email,

        @NotNull(message = "{userDTO.personal.notNull}")
        @Valid
        PersonalDTO personal,

        @NotNull(message = "{userDTO.physical.notNull}")
        @Valid
        PhysicalDTO physical,

        @NotNull(message = "{userDTO.settings.notNull}")
        @Valid
        AlarmSettingsDTO settings
) {
}
