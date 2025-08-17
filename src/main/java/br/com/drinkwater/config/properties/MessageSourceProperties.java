package br.com.drinkwater.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Immutable configuration properties for message source settings.
 * All properties are loaded at bootstrap time and cannot be modified at runtime.
 */
@ConfigurationProperties(prefix = "message-source")
@Validated
public record MessageSourceProperties(

        @NotBlank(message = "MESSAGE_SOURCE_ENCODING environment variable is required")
        String encoding
) {

    /**
     * Constructor with strict validation - no defaults.
     */
    public MessageSourceProperties {
        // All properties are required - no defaults allowed
    }

    /**
     * Validates that the encoding is a valid charset.
     *
     * @return true if encoding is valid
     */
    public boolean isValidEncoding() {
        try {
            java.nio.charset.Charset.forName(encoding);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}