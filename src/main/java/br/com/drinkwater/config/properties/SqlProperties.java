package br.com.drinkwater.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Immutable configuration properties for SQL initialization settings.
 * All properties are loaded at bootstrap time and cannot be modified at runtime.
 * Maps to spring.sql.* configuration properties.
 */
@ConfigurationProperties(prefix = "spring.sql")
@Validated
public record SqlProperties(

        /**
         * SQL initialization configuration.
         * Maps to spring.sql.init.* properties.
         */
        SqlInitProperties init
) {

    /**
     * Constructor with strict validation - no defaults.
     */
    public SqlProperties {
        // All properties are required - no defaults allowed
    }

    /**
     * Checks if the SQL init mode is safe for production.
     *
     * @return true if init mode is 'never' or 'embedded'
     */
    public boolean isProductionSafe() {
        return init != null && init.isProductionSafe();
    }

    /**
     * Nested properties for SQL initialization configuration.
     */
    @Validated
    public record SqlInitProperties(
            @NotBlank(message = "SQL_INIT_MODE environment variable is required")
            @Pattern(
                    regexp = "^(always|embedded|never)$",
                    message = "SQL_INIT_MODE must be one of: always, embedded, never"
            )
            String mode
    ) {
        /**
         * Checks if the SQL init mode is safe for production.
         *
         * @return true if init mode is 'never' or 'embedded'
         */
        public boolean isProductionSafe() {
            return "never".equals(mode) || "embedded".equals(mode);
        }
    }
}