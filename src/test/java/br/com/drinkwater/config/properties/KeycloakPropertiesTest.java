package br.com.drinkwater.config.properties;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for KeycloakProperties validation to ensure fail-fast behavior
 * for invalid Keycloak configuration.
 */
class KeycloakPropertiesTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    void givenValidKeycloakProperties_whenValidate_thenShouldPass() {
        // Given
        KeycloakProperties properties = new KeycloakProperties(
            "https://auth.example.com",
            "drinkwater",
            "drinkwaterapp",
            "admin",
            "adminpassword",
            "https://auth.example.com/realms/drinkwater",
            "https://auth.example.com/realms/drinkwater/protocol/openid-connect/certs"
        );
        
        // When
        Set<ConstraintViolation<KeycloakProperties>> violations = validator.validate(properties);
        
        // Then
        assertTrue(violations.isEmpty(), "Valid properties should not have violations");
        assertTrue(properties.isSecureConnection());
        assertTrue(properties.isIssuerUriValid());
        assertTrue(properties.isJwkSetUriValid());
    }
    
    @Test
    void givenInvalidUrl_whenValidate_thenShouldFail() {
        // Given
        KeycloakProperties properties = new KeycloakProperties(
            "invalid-url", // Invalid URL
            "drinkwater",
            "drinkwaterapp",
            "admin",
            "adminpassword",
            "https://auth.example.com/realms/drinkwater",
            "https://auth.example.com/realms/drinkwater/protocol/openid-connect/certs"
        );
        
        // When
        Set<ConstraintViolation<KeycloakProperties>> violations = validator.validate(properties);
        
        // Then
        assertFalse(violations.isEmpty(), "Invalid URL should have violations");
        assertTrue(violations.stream().anyMatch(v -> 
            v.getMessage().contains("must be a valid HTTP or HTTPS URL")));
    }
    
    @Test
    void givenShortPassword_whenValidate_thenShouldFail() {
        // Given
        KeycloakProperties properties = new KeycloakProperties(
            "https://auth.example.com",
            "drinkwater",
            "drinkwaterapp",
            "admin",
            "short", // Too short
            "https://auth.example.com/realms/drinkwater",
            "https://auth.example.com/realms/drinkwater/protocol/openid-connect/certs"
        );
        
        // When
        Set<ConstraintViolation<KeycloakProperties>> violations = validator.validate(properties);
        
        // Then
        assertFalse(violations.isEmpty(), "Short password should have violations");
        assertTrue(violations.stream().anyMatch(v -> 
            v.getMessage().contains("must be at least 8 characters")));
    }
    
    @Test
    void givenInconsistentIssuerUri_whenValidate_thenShouldDetectInconsistency() {
        // Given
        KeycloakProperties properties = new KeycloakProperties(
            "https://auth.example.com",
            "drinkwater",
            "drinkwaterapp",
            "admin",
            "adminpassword",
            "https://different.example.com/realms/drinkwater", // Inconsistent
            "https://auth.example.com/realms/drinkwater/protocol/openid-connect/certs"
        );
        
        // When
        boolean isValid = properties.isIssuerUriValid();
        
        // Then
        assertFalse(isValid, "Inconsistent issuer URI should be detected");
    }
    
    @Test
    void givenInconsistentJwkSetUri_whenValidate_thenShouldDetectInconsistency() {
        // Given
        KeycloakProperties properties = new KeycloakProperties(
            "https://auth.example.com",
            "drinkwater",
            "drinkwaterapp",
            "admin",
            "adminpassword",
            "https://auth.example.com/realms/drinkwater",
            "https://auth.example.com/realms/different/protocol/openid-connect/certs" // Wrong realm
        );
        
        // When
        boolean isValid = properties.isJwkSetUriValid();
        
        // Then
        assertFalse(isValid, "Inconsistent JWK Set URI should be detected");
    }
    
    @Test
    void givenHttpUrl_whenCheckSecurity_thenShouldNotBeSecure() {
        // Given
        KeycloakProperties properties = new KeycloakProperties(
            "http://localhost:8080", // HTTP instead of HTTPS
            "drinkwater",
            "drinkwaterapp",
            "admin",
            "adminpassword",
            "http://localhost:8080/realms/drinkwater",
            "http://localhost:8080/realms/drinkwater/protocol/openid-connect/certs"
        );
        
        // When
        boolean isSecure = properties.isSecureConnection();
        
        // Then
        assertFalse(isSecure, "HTTP URLs should not be considered secure");
    }
    
}