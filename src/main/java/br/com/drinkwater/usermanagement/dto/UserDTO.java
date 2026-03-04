package br.com.drinkwater.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Request DTO for creating or updating a user profile. */
@Schema(description = "Request payload for creating or updating a user profile")
public record UserDTO(
        @Schema(description = "User email address", example = "john.doe@example.com")
                @NotBlank(message = "{user.email.not-blank}")
                @Email(message = "{user.email.email}")
                @Pattern(
                        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                        message = "{user.email.pattern}")
                @Size(max = 255, message = "{user.email.size}")
                String email,
        @Schema(description = "Personal information")
                @NotNull(message = "{user.personal.not-null}")
                @Valid
                PersonalDTO personal,
        @Schema(description = "Physical measurements")
                @NotNull(message = "{user.physical.not-null}")
                @Valid
                PhysicalDTO physical,
        @Schema(description = "Alarm settings for hydration reminders")
                @NotNull(message = "{user.settings.not-null}")
                @Valid
                AlarmSettingsDTO settings) {}
