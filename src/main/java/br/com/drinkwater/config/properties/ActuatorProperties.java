package br.com.drinkwater.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Set;

/**
 * Immutable configuration properties for Spring Boot Actuator endpoints.
 * All properties are loaded at bootstrap time and cannot be modified at runtime.
 */
@ConfigurationProperties(prefix = "actuator")
@Validated
public record ActuatorProperties(

        @NotNull(message = "ACTUATOR_ENDPOINTS environment variable is required")
        List<String> endpoints,

        @NotBlank(message = "ACTUATOR_BASE_PATH environment variable is required")
        @Pattern(
                regexp = "^/[a-zA-Z0-9/_-]*$",
                message = "ACTUATOR_BASE_PATH must start with '/' and contain only alphanumeric characters, hyphens, and underscores"
        )
        String basePath,

        @NotBlank(message = "ACTUATOR_HEALTH_SHOW_DETAILS environment variable is required")
        @Pattern(
                regexp = "^(never|when-authorized|always)$",
                message = "ACTUATOR_HEALTH_SHOW_DETAILS must be one of: never, when-authorized, always"
        )
        String healthShowDetails,

        @NotBlank(message = "ACTUATOR_HEALTH_SHOW_COMPONENTS environment variable is required")
        @Pattern(
                regexp = "^(never|when-authorized|always)$",
                message = "ACTUATOR_HEALTH_SHOW_COMPONENTS must be one of: never, when-authorized, always"
        )
        String healthShowComponents
) {

    // Valid actuator endpoints
    private static final Set<String> VALID_ENDPOINTS = Set.of(
            "health", "info", "metrics", "prometheus", "beans", "env",
            "configprops", "loggers", "httptrace", "threaddump", "heapdump"
    );
    // Production-safe endpoints
    private static final Set<String> PRODUCTION_SAFE_ENDPOINTS = Set.of("health", "info", "metrics", "prometheus");

    /**
     * Constructor with strict validation - no defaults.
     */
    public ActuatorProperties {
        // Validate endpoints
        if (endpoints != null) {
            for (String endpoint : endpoints) {
                if (!VALID_ENDPOINTS.contains(endpoint)) {
                    throw new IllegalArgumentException("Invalid actuator endpoint: " + endpoint + ". Valid endpoints are: " + VALID_ENDPOINTS);
                }
            }
        }
    }

    /**
     * Checks if the configuration is safe for production.
     *
     * @return true if only production-safe endpoints are exposed
     */
    public boolean isProductionSafe() {
        return PRODUCTION_SAFE_ENDPOINTS.containsAll(endpoints) && "never".equals(healthShowDetails);
    }

    /**
     * Checks if development endpoints are exposed.
     *
     * @return true if development-specific endpoints are enabled
     */
    public boolean hasDevelopmentEndpoints() {
        return endpoints.stream().anyMatch(endpoint -> !PRODUCTION_SAFE_ENDPOINTS.contains(endpoint));
    }

    /**
     * Gets the list of enabled endpoints as a comma-separated string.
     *
     * @return comma-separated endpoint names
     */
    public String getEndpointsAsString() {
        return String.join(",", endpoints);
    }
}