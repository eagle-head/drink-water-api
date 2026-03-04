package br.com.drinkwater.config.properties;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Tests for LoggingProperties record to ensure isProductionSafe and isDevelopmentMode delegate
 * correctly to LoggingLevelValidator.
 */
final class LoggingPropertiesTest {

    @Test
    void givenWarnAndErrorLevels_whenIsProductionSafe_thenReturnsTrue() {
        // Given
        LoggingProperties properties =
                new LoggingProperties("WARN", "WARN", "WARN", "WARN", "WARN", "WARN", "WARN");

        // When
        boolean result = properties.isProductionSafe();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenDebugInSecurity_whenIsProductionSafe_thenReturnsFalse() {
        // Given
        LoggingProperties properties =
                new LoggingProperties("WARN", "INFO", "DEBUG", "WARN", "WARN", "WARN", "WARN");

        // When
        boolean result = properties.isProductionSafe();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void givenDebugRoot_whenIsDevelopmentMode_thenReturnsTrue() {
        // Given
        LoggingProperties properties =
                new LoggingProperties("DEBUG", "INFO", "WARN", "WARN", "INFO", "WARN", "WARN");

        // When
        boolean result = properties.isDevelopmentMode();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenDebugSql_whenIsDevelopmentMode_thenReturnsTrue() {
        // Given
        LoggingProperties properties =
                new LoggingProperties("INFO", "INFO", "WARN", "WARN", "DEBUG", "WARN", "WARN");

        // When
        boolean result = properties.isDevelopmentMode();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenInfoRootAndInfoSql_whenIsDevelopmentMode_thenReturnsFalse() {
        // Given
        LoggingProperties properties =
                new LoggingProperties("INFO", "INFO", "WARN", "WARN", "INFO", "WARN", "WARN");

        // When
        boolean result = properties.isDevelopmentMode();

        // Then
        assertThat(result).isFalse();
    }
}
