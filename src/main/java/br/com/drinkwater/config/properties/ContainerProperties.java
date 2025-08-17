package br.com.drinkwater.config.properties;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Immutable configuration properties for container settings and resource limits.
 * All properties are loaded at bootstrap time and cannot be modified at runtime.
 */
@ConfigurationProperties(prefix = "container")
@Validated
public record ContainerProperties(

        // Image versions
        Images images,
        
        // Resource limits
        Resources resources
) {

    /**
     * Constructor with strict validation - no defaults.
     */
    public ContainerProperties {
        // All properties are required - no defaults allowed
    }

    /**
     * Container image versions configuration.
     */
    public record Images(
            @NotBlank(message = "POSTGRES_VERSION environment variable is required")
            String postgresVersion,

            @NotBlank(message = "KEYCLOAK_VERSION environment variable is required")
            String keycloakVersion,

            @NotBlank(message = "KEYCLOAK_POSTGRES_VERSION environment variable is required")
            String keycloakPostgresVersion
    ) {}

    /**
     * Container resource limits configuration.
     */
    public record Resources(
            PostgresResources postgres,
            KeycloakResources keycloak,
            KeycloakDbResources keycloakDb
    ) {}

    /**
     * PostgreSQL container resource configuration.
     */
    public record PostgresResources(
            @NotBlank(message = "POSTGRES_MEMORY_LIMIT environment variable is required")
            String memoryLimit,

            @NotBlank(message = "POSTGRES_MEMORY_RESERVATION environment variable is required")
            String memoryReservation,

            @NotNull(message = "POSTGRES_CPU_LIMIT environment variable is required")
            @DecimalMin(value = "0.1", message = "POSTGRES_CPU_LIMIT must be at least 0.1")
            @DecimalMax(value = "4.0", message = "POSTGRES_CPU_LIMIT cannot exceed 4.0")
            Double cpuLimit
    ) {}

    /**
     * Keycloak container resource configuration.
     */
    public record KeycloakResources(
            @NotBlank(message = "KEYCLOAK_MEMORY_LIMIT environment variable is required")
            String memoryLimit,

            @NotBlank(message = "KEYCLOAK_MEMORY_RESERVATION environment variable is required")
            String memoryReservation,

            @NotNull(message = "KEYCLOAK_CPU_LIMIT environment variable is required")
            @DecimalMin(value = "0.1", message = "KEYCLOAK_CPU_LIMIT must be at least 0.1")
            @DecimalMax(value = "4.0", message = "KEYCLOAK_CPU_LIMIT cannot exceed 4.0")
            Double cpuLimit
    ) {}

    /**
     * Keycloak database container resource configuration.
     */
    public record KeycloakDbResources(
            @NotBlank(message = "KEYCLOAK_DB_MEMORY_LIMIT environment variable is required")
            String memoryLimit
    ) {}
}