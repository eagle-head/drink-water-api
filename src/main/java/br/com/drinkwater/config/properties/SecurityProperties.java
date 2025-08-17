package br.com.drinkwater.config.properties;

import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Immutable configuration properties for security settings.
 * All properties are loaded at bootstrap time and cannot be modified at runtime.
 */
@ConfigurationProperties(prefix = "security")
@Validated
public record SecurityProperties(

        String jwtSigningKey,

        String encryptionKey,

        String apiSecretKey
) {

    /**
     * Constructor with strict validation - no defaults.
     */
    public SecurityProperties {
        // All properties are required - no defaults allowed
    }


    /**
     * Validates that security keys are present for production environments.
     *
     * @param isProduction whether this is a production environment
     * @return true if all required keys are present for production
     */
    public boolean areKeysValidForEnvironment(boolean isProduction) {
        if (!isProduction) {
            return true; // Keys are optional in non-production
        }

        return jwtSigningKey != null && !jwtSigningKey.trim().isEmpty() &&
                encryptionKey != null && !encryptionKey.trim().isEmpty() &&
                apiSecretKey != null && !apiSecretKey.trim().isEmpty();
    }
}