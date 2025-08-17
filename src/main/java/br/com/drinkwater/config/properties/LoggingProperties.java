package br.com.drinkwater.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Immutable configuration properties for logging settings.
 * All properties are loaded at bootstrap time and cannot be modified at runtime.
 */
@ConfigurationProperties(prefix = "logging.level")
@Validated
public record LoggingProperties(

        @NotBlank(message = "LOGGING_LEVEL_ROOT environment variable is required")
        @Pattern(
                regexp = "^(TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)$",
                message = "LOGGING_LEVEL_ROOT must be one of: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF (case-sensitive, must be uppercase). Current value: '${validatedValue}'"
        )
        String root,

        @NotBlank(message = "LOGGING_LEVEL_APP environment variable is required")
        @Pattern(
                regexp = "^(TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)$",
                message = "LOGGING_LEVEL_APP must be one of: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF"
        )
        String app,

        @NotBlank(message = "LOGGING_LEVEL_SECURITY environment variable is required")
        @Pattern(
                regexp = "^(TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)$",
                message = "LOGGING_LEVEL_SECURITY must be one of: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF"
        )
        String security,

        @NotBlank(message = "LOGGING_LEVEL_OAUTH2 environment variable is required")
        @Pattern(
                regexp = "^(TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)$",
                message = "LOGGING_LEVEL_OAUTH2 must be one of: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF"
        )
        String oauth2,

        @NotBlank(message = "LOGGING_LEVEL_SQL environment variable is required")
        @Pattern(
                regexp = "^(TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)$",
                message = "LOGGING_LEVEL_SQL must be one of: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF"
        )
        String sql,

        @NotBlank(message = "LOGGING_LEVEL_SQL_PARAMS environment variable is required")
        @Pattern(
                regexp = "^(TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)$",
                message = "LOGGING_LEVEL_SQL_PARAMS must be one of: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF"
        )
        String sqlParams,

        @NotBlank(message = "LOGGING_LEVEL_HIBERNATE environment variable is required")
        @Pattern(
                regexp = "^(TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)$",
                message = "LOGGING_LEVEL_HIBERNATE must be one of: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF"
        )
        String hibernate,

        @NotBlank(message = "LOGGING_LEVEL_SPRINGFRAMEWORK environment variable is required")
        @Pattern(
                regexp = "^(TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)$",
                message = "LOGGING_LEVEL_SPRINGFRAMEWORK must be one of: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF"
        )
        String springframework
) {

    /**
     * Constructor with strict validation - no defaults.
     */
    public LoggingProperties {
        // All properties are required - no defaults allowed
    }

    /**
     * Checks if logging is configured appropriately for production.
     *
     * @return true if log levels are WARN or higher for production
     */
    public boolean isProductionSafe() {
        return isLevelSafeForProduction(root) &&
                isLevelSafeForProduction(security) &&
                isLevelSafeForProduction(oauth2) &&
                isLevelSafeForProduction(sql);
    }

    /**
     * Checks if logging is configured for development (verbose logging).
     *
     * @return true if debug logging is enabled
     */
    public boolean isDevelopmentMode() {
        return "DEBUG".equals(root) || "TRACE".equals(root) ||
                "DEBUG".equals(sql) || "TRACE".equals(sql);
    }

    private boolean isLevelSafeForProduction(String level) {
        return "WARN".equals(level) || "ERROR".equals(level) ||
                "FATAL".equals(level) || "OFF".equals(level);
    }
}