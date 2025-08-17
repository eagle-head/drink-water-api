package br.com.drinkwater.config.properties;

import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Immutable configuration properties for database connectivity and JPA settings.
 * All properties are loaded at bootstrap time and cannot be modified at runtime.
 */
@ConfigurationProperties(prefix = "spring.datasource")
@Validated
public record DatabaseProperties(

        @NotBlank(message = "DATABASE_URL environment variable is required")
        @Pattern(
                regexp = "^jdbc:postgresql://.*",
                message = "DATABASE_URL must be a valid PostgreSQL JDBC URL starting with 'jdbc:postgresql://'"
        )
        String url,

        @NotBlank(message = "DATABASE_USERNAME environment variable is required")
        @Size(min = 1, max = 63, message = "DATABASE_USERNAME must be between 1 and 63 characters")
        String username,

        @NotBlank(message = "DATABASE_PASSWORD environment variable is required")
        @Size(min = 8, message = "DATABASE_PASSWORD must be at least 8 characters for security")
        String password,

        @NotBlank(message = "DATABASE_DRIVER environment variable is required")
        @Pattern(
                regexp = "^org\\.postgresql\\.Driver$",
                message = "DATABASE_DRIVER must be 'org.postgresql.Driver'"
        )
        String driverClassName,

        @NotNull(message = "DATABASE_POOL_SIZE environment variable is required")
        @Min(value = 1, message = "DATABASE_POOL_SIZE must be at least 1")
        @Max(value = 100, message = "DATABASE_POOL_SIZE cannot exceed 100 for resource management")
        Integer poolSize,

        @NotNull(message = "DATABASE_MIN_IDLE environment variable is required")
        @Min(value = 0, message = "DATABASE_MIN_IDLE cannot be negative")
        @Max(value = 50, message = "DATABASE_MIN_IDLE cannot exceed 50")
        Integer minIdle,

        @NotNull(message = "DATABASE_CONNECTION_TIMEOUT environment variable is required")
        @Min(value = 1000, message = "DATABASE_CONNECTION_TIMEOUT must be at least 1000ms")
        @Max(value = 300000, message = "DATABASE_CONNECTION_TIMEOUT cannot exceed 300000ms (5 minutes)")
        Long connectionTimeout
) {

    /**
     * Constructor with strict validation - no defaults.
     */
    public DatabaseProperties {
        // All properties are required - no defaults allowed
    }

    /**
     * Validates that minIdle is not greater than poolSize.
     */
    @AssertTrue(message = "DATABASE_MIN_IDLE cannot be greater than DATABASE_POOL_SIZE")
    public boolean isMinIdleValid() {
        return minIdle <= poolSize;
    }
}