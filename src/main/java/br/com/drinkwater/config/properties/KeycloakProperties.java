package br.com.drinkwater.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Immutable configuration properties for Keycloak OAuth2 integration.
 * All properties are loaded at bootstrap time and cannot be modified at runtime.
 */
@ConfigurationProperties(prefix = "keycloak")
@Validated
public record KeycloakProperties(

        @NotBlank(message = "KEYCLOAK_URL environment variable is required")
        @Pattern(
                regexp = "^https?://.*",
                message = "KEYCLOAK_URL must be a valid HTTP or HTTPS URL"
        )
        String url,

        @NotBlank(message = "KEYCLOAK_REALM environment variable is required")
        @Size(min = 1, max = 255, message = "KEYCLOAK_REALM must be between 1 and 255 characters")
        String realm,

        @NotBlank(message = "KEYCLOAK_CLIENT_ID environment variable is required")
        @Size(min = 1, max = 255, message = "KEYCLOAK_CLIENT_ID must be between 1 and 255 characters")
        String clientId,

        @NotBlank(message = "KEYCLOAK_USERNAME environment variable is required")
        String username,

        @NotBlank(message = "KEYCLOAK_PASSWORD environment variable is required")
        @Size(min = 8, message = "KEYCLOAK_PASSWORD must be at least 8 characters for security")
        String password,

        @NotBlank(message = "KEYCLOAK_ISSUER_URI environment variable is required")
        @Pattern(
                regexp = "^https?://.*",
                message = "KEYCLOAK_ISSUER_URI must be a valid HTTP or HTTPS URL"
        )
        String issuerUri,

        @NotBlank(message = "KEYCLOAK_JWK_SET_URI environment variable is required")
        @Pattern(
                regexp = "^https?://.*",
                message = "KEYCLOAK_JWK_SET_URI must be a valid HTTP or HTTPS URL"
        )
        String jwkSetUri
) {

    /**
     * Constructor with strict validation - no defaults.
     */
    public KeycloakProperties {
        // All properties are required - no defaults allowed
    }

    /**
     * Validates that issuer URI is consistent with Keycloak URL and realm.
     *
     * @return true if issuer URI follows expected pattern
     */
    public boolean isIssuerUriValid() {
        String expectedPrefix = url + "/realms/" + realm;
        return issuerUri.startsWith(expectedPrefix);
    }

    /**
     * Validates that JWK Set URI is consistent with Keycloak URL and realm.
     *
     * @return true if JWK Set URI follows expected pattern
     */
    public boolean isJwkSetUriValid() {
        String expectedPrefix = url + "/realms/" + realm + "/protocol/openid-connect/certs";
        return jwkSetUri.equals(expectedPrefix);
    }

    /**
     * Checks if Keycloak is configured for production (HTTPS).
     *
     * @return true if URL uses HTTPS
     */
    public boolean isSecureConnection() {
        return url.startsWith("https://");
    }
}