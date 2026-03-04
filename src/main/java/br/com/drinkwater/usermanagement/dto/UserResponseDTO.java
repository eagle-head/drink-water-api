package br.com.drinkwater.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

/** Response DTO representing a user profile returned by the API. */
@Schema(description = "User profile")
public record UserResponseDTO(
        @Schema(
                        description = "Keycloak public ID",
                        example = "fbc58717-5d48-4041-9f1c-257e8052428f")
                UUID publicId,
        @Schema(description = "User email address", example = "john.doe@example.com") String email,
        @Schema(description = "Personal information") PersonalDTO personal,
        @Schema(description = "Physical measurements") PhysicalDTO physical,
        @Schema(description = "Alarm settings") AlarmSettingsResponseDTO settings) {}
