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
 * Tests for DatabaseProperties validation to ensure fail-fast behavior
 * for invalid database configuration.
 */
class DatabasePropertiesTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    void givenValidDatabaseProperties_whenValidate_thenShouldPass() {
        // Given
        DatabaseProperties properties = new DatabaseProperties(
            "jdbc:postgresql://localhost:5432/testdb",
            "testuser",
            "testpassword123", // Longer password to meet 8-char requirement
            "org.postgresql.Driver",
            10, 2, 30000L
        );
        
        // When
        Set<ConstraintViolation<DatabaseProperties>> violations = validator.validate(properties);
        
        // Then
        if (!violations.isEmpty()) {
            System.out.println("Validation errors:");
            violations.forEach(v -> System.out.println("- " + v.getPropertyPath() + ": " + v.getMessage()));
        }
        assertTrue(violations.isEmpty(), "Valid properties should not have violations");
    }
    
    @Test
    void givenInvalidJdbcUrl_whenValidate_thenShouldFail() {
        // Given
        DatabaseProperties properties = new DatabaseProperties(
            "jdbc:mysql://localhost:3306/testdb", // Invalid - not PostgreSQL
            "testuser",
            "testpassword",
            "org.postgresql.Driver",
            10, 2, 30000L
        );
        
        // When
        Set<ConstraintViolation<DatabaseProperties>> violations = validator.validate(properties);
        
        // Then
        assertFalse(violations.isEmpty(), "Invalid JDBC URL should have violations");
        assertTrue(violations.stream().anyMatch(v -> 
            v.getMessage().contains("must be a valid PostgreSQL JDBC URL")));
    }
    
    @Test
    void givenShortPassword_whenValidate_thenShouldFail() {
        // Given
        DatabaseProperties properties = new DatabaseProperties(
            "jdbc:postgresql://localhost:5432/testdb",
            "testuser",
            "short", // Too short
            "org.postgresql.Driver",
            10, 2, 30000L
        );
        
        // When
        Set<ConstraintViolation<DatabaseProperties>> violations = validator.validate(properties);
        
        // Then
        assertFalse(violations.isEmpty(), "Short password should have violations");
        assertTrue(violations.stream().anyMatch(v -> 
            v.getMessage().contains("must be at least 8 characters")));
    }
    
    @Test
    void givenInvalidPoolSizes_whenValidate_thenShouldFail() {
        // Given
        DatabaseProperties properties = new DatabaseProperties(
            "jdbc:postgresql://localhost:5432/testdb",
            "testuser",
            "testpassword",
            "org.postgresql.Driver",
            5, 10, 30000L // minIdle > poolSize
        );
        
        // When
        Set<ConstraintViolation<DatabaseProperties>> violations = validator.validate(properties);
        
        // Then
        assertFalse(violations.isEmpty(), "Invalid pool configuration should have violations");
        assertTrue(violations.stream().anyMatch(v -> 
            v.getMessage().contains("cannot be greater than")));
    }
    
    @Test
    void givenNullRequiredField_whenValidate_thenShouldFail() {
        // Given
        DatabaseProperties properties = new DatabaseProperties(
            null, // Required field
            "testuser",
            "testpassword",
            "org.postgresql.Driver",
            10, 2, 30000L
        );
        
        // When
        Set<ConstraintViolation<DatabaseProperties>> violations = validator.validate(properties);
        
        // Then
        assertFalse(violations.isEmpty(), "Null required field should have violations");
        assertTrue(violations.stream().anyMatch(v -> 
            v.getMessage().contains("DATABASE_URL environment variable is required")));
    }
    
    @Test
    void givenAllRequiredValues_whenCreate_thenShouldSetProperties() {
        // Given & When - All properties are required (no defaults)
        DatabaseProperties properties = new DatabaseProperties(
            "jdbc:postgresql://localhost:5432/testdb",
            "testuser",
            "testpassword",
            "org.postgresql.Driver",
            10, 2, 30000L // Explicit values required
        );
        
        // Then
        assertEquals(10, properties.poolSize());
        assertEquals(2, properties.minIdle());
        assertEquals(30000L, properties.connectionTimeout());
    }
}