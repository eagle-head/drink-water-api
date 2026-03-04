package br.com.drinkwater.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Immutable configuration properties for Jackson serialization settings. All properties are loaded
 * at bootstrap time and cannot be modified at runtime. Provides fail-fast validation for
 * JACKSON_TIMEZONE and JACKSON_WRITE_DATES_AS_TIMESTAMPS environment variables.
 */
@ConfigurationProperties(prefix = "jackson-config")
@Validated
public record JacksonProperties(
        @NotBlank(message = "JACKSON_TIMEZONE environment variable is required") String timeZone,
        @NotNull(message = "JACKSON_WRITE_DATES_AS_TIMESTAMPS environment variable is required")
                @Valid
                JacksonSerializationProperties serialization) {

    /** Constructor with strict validation - no defaults. */
    public JacksonProperties {
        // All properties are required - no defaults allowed
    }

    /** Nested properties for Jackson serialization configuration. */
    @Validated
    public record JacksonSerializationProperties(
            @NotNull(
                            message =
                                    "JACKSON_WRITE_DATES_AS_TIMESTAMPS environment variable is"
                                            + " required")
                    Boolean writeDatesAsTimestamps) {}
}
