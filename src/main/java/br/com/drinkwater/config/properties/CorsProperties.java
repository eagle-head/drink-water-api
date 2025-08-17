package br.com.drinkwater.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Immutable configuration properties for CORS settings.
 * All properties are loaded at bootstrap time and cannot be modified at runtime.
 */
@ConfigurationProperties(prefix = "cors")
@Validated
public record CorsProperties(

        @NotBlank(message = "BASE_URL environment variable is required")
        @Pattern(
                regexp = "^https?://.*",
                message = "BASE_URL must be a valid HTTP or HTTPS URL"
        )
        String baseUrl,

        @NotBlank(message = "CORS_ALLOWED_ORIGIN environment variable is required")
        @Pattern(
                regexp = "^https?://.*",
                message = "CORS_ALLOWED_ORIGIN must be a valid HTTP or HTTPS URL"
        )
        String allowedOrigin,

        @NotNull(message = "CORS_ALLOWED_ORIGINS environment variable is required")
        List<String> allowedOrigins,

        @NotNull(message = "CORS_ALLOWED_METHODS environment variable is required")
        List<String> allowedMethods,

        @NotNull(message = "CORS_ALLOWED_HEADERS environment variable is required")
        List<String> allowedHeaders,

        @NotNull(message = "CORS_ALLOW_CREDENTIALS environment variable is required")
        Boolean allowCredentials,

        @NotNull(message = "CORS_MAX_AGE environment variable is required")
        @Positive(message = "CORS_MAX_AGE must be positive")
        Long maxAge
) {

    /**
     * Constructor with strict validation - no defaults.
     */
    public CorsProperties {
        // All properties are required - no defaults allowed
        // Parse comma-separated origins if needed for validation
        if (allowedOrigins == null && allowedOrigin != null) {
            allowedOrigins = List.of(allowedOrigin.split(","));
        }
    }

    /**
     * Validates that the base URL is included in allowed origins.
     *
     * @return true if base URL is in the allowed origins list
     */
    public boolean isBaseUrlAllowed() {
        return allowedOrigins.contains(baseUrl);
    }

    /**
     * Checks if all origins use secure connections (HTTPS) for production.
     *
     * @return true if all origins use HTTPS
     */
    public boolean areOriginsSecure() {
        return allowedOrigins.stream().allMatch(origin -> origin.startsWith("https://"));
    }

    /**
     * Gets the primary allowed origin (first in the list).
     *
     * @return the primary origin URL
     */
    public String getPrimaryOrigin() {
        return allowedOrigins.isEmpty() ? allowedOrigin : allowedOrigins.get(0);
    }
}