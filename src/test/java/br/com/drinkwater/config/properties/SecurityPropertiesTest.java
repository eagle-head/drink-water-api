package br.com.drinkwater.config.properties;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Tests for SecurityProperties record to ensure key validation behaves correctly for production and
 * non-production environments.
 */
final class SecurityPropertiesTest {

    @Test
    void givenNonProductionEnvironment_whenAreKeysValidForEnvironment_thenReturnsTrue() {
        // Given
        SecurityProperties properties = new SecurityProperties(null, null, null);

        // When
        boolean result = properties.areKeysValidForEnvironment(false);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenProductionEnvironmentWithAllKeys_whenAreKeysValidForEnvironment_thenReturnsTrue() {
        // Given
        SecurityProperties properties =
                new SecurityProperties("jwt-key", "encryption-key", "api-secret-key");

        // When
        boolean result = properties.areKeysValidForEnvironment(true);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void
            givenProductionEnvironmentWithNullJwtSigningKey_whenAreKeysValidForEnvironment_thenReturnsFalse() {
        // Given
        SecurityProperties properties =
                new SecurityProperties(null, "encryption-key", "api-secret-key");

        // When
        boolean result = properties.areKeysValidForEnvironment(true);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void
            givenProductionEnvironmentWithEmptyJwtSigningKey_whenAreKeysValidForEnvironment_thenReturnsFalse() {
        // Given
        SecurityProperties properties =
                new SecurityProperties("", "encryption-key", "api-secret-key");

        // When
        boolean result = properties.areKeysValidForEnvironment(true);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void
            givenProductionEnvironmentWithBlankJwtSigningKey_whenAreKeysValidForEnvironment_thenReturnsFalse() {
        // Given
        SecurityProperties properties =
                new SecurityProperties("   ", "encryption-key", "api-secret-key");

        // When
        boolean result = properties.areKeysValidForEnvironment(true);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void
            givenProductionEnvironmentWithNullEncryptionKey_whenAreKeysValidForEnvironment_thenReturnsFalse() {
        // Given
        SecurityProperties properties = new SecurityProperties("jwt-key", null, "api-secret-key");

        // When
        boolean result = properties.areKeysValidForEnvironment(true);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void
            givenProductionEnvironmentWithEmptyEncryptionKey_whenAreKeysValidForEnvironment_thenReturnsFalse() {
        // Given
        SecurityProperties properties = new SecurityProperties("jwt-key", "", "api-secret-key");

        // When
        boolean result = properties.areKeysValidForEnvironment(true);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void
            givenProductionEnvironmentWithNullApiSecretKey_whenAreKeysValidForEnvironment_thenReturnsFalse() {
        // Given
        SecurityProperties properties = new SecurityProperties("jwt-key", "encryption-key", null);

        // When
        boolean result = properties.areKeysValidForEnvironment(true);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void
            givenProductionEnvironmentWithEmptyApiSecretKey_whenAreKeysValidForEnvironment_thenReturnsFalse() {
        // Given
        SecurityProperties properties = new SecurityProperties("jwt-key", "encryption-key", "");

        // When
        boolean result = properties.areKeysValidForEnvironment(true);

        // Then
        assertThat(result).isFalse();
    }
}
