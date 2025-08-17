package br.com.drinkwater.config.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Immutable configuration properties for server settings.
 * All properties are loaded at bootstrap time and cannot be modified at runtime.
 */
@ConfigurationProperties(prefix = "server")
@Validated
public record ServerProperties(

        @NotNull(message = "SERVER_PORT environment variable is required")
        @Min(value = 1024, message = "SERVER_PORT must be >= 1024 (privileged ports not allowed)")
        @Max(value = 65535, message = "SERVER_PORT must be <= 65535")
        Integer port,

        @NotNull(message = "ERROR_STACKTRACE_POLICY environment variable is required")
        @Pattern(
                regexp = "^(never|on_param|always)$",
                message = "ERROR_STACKTRACE_POLICY must be one of: never, on_param, always"
        )
        String errorStacktracePolicy
) {

    /**
     * Constructor with strict validation - no defaults.
     */
    public ServerProperties {
        // All properties are required - no defaults allowed
    }
}