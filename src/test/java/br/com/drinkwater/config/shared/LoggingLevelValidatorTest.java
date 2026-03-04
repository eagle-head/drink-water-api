package br.com.drinkwater.config.shared;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Tests for LoggingLevelValidator utility class to ensure isProductionSafe and isDevelopmentMode
 * evaluate logging levels correctly.
 */
final class LoggingLevelValidatorTest {

    @Test
    void givenAllWarnLevels_whenIsProductionSafe_thenReturnsTrue() {
        // Given
        String root = "WARN";
        String security = "WARN";
        String oauth2 = "WARN";
        String sql = "WARN";

        // When
        boolean result = LoggingLevelValidator.isProductionSafe(root, security, oauth2, sql);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenOneDebugLevel_whenIsProductionSafe_thenReturnsFalse() {
        // Given
        String root = "WARN";
        String security = "DEBUG";
        String oauth2 = "WARN";
        String sql = "WARN";

        // When
        boolean result = LoggingLevelValidator.isProductionSafe(root, security, oauth2, sql);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void givenTraceRoot_whenIsDevelopmentMode_thenReturnsTrue() {
        // Given
        String root = "TRACE";
        String sql = "INFO";

        // When
        boolean result = LoggingLevelValidator.isDevelopmentMode(root, sql);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenDebugRoot_whenIsDevelopmentMode_thenReturnsTrue() {
        // Given
        String root = "DEBUG";
        String sql = "INFO";

        // When
        boolean result = LoggingLevelValidator.isDevelopmentMode(root, sql);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenDebugSql_whenIsDevelopmentMode_thenReturnsTrue() {
        // Given
        String root = "INFO";
        String sql = "DEBUG";

        // When
        boolean result = LoggingLevelValidator.isDevelopmentMode(root, sql);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenTraceSql_whenIsDevelopmentMode_thenReturnsTrue() {
        // Given
        String root = "INFO";
        String sql = "TRACE";

        // When
        boolean result = LoggingLevelValidator.isDevelopmentMode(root, sql);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenInfoRootAndInfoSql_whenIsDevelopmentMode_thenReturnsFalse() {
        // Given
        String root = "INFO";
        String sql = "INFO";

        // When
        boolean result = LoggingLevelValidator.isDevelopmentMode(root, sql);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void givenUnsafeOauth2Level_whenIsProductionSafe_thenReturnsFalse() {
        // Given
        boolean result = LoggingLevelValidator.isProductionSafe("WARN", "WARN", "DEBUG", "WARN");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void givenUnsafeSqlLevel_whenIsProductionSafe_thenReturnsFalse() {
        // Given
        boolean result = LoggingLevelValidator.isProductionSafe("WARN", "WARN", "WARN", "TRACE");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void givenErrorAndFatalLevels_whenIsProductionSafe_thenReturnsTrue() {
        // Given
        String root = "ERROR";
        String security = "FATAL";
        String oauth2 = "OFF";
        String sql = "WARN";

        // When
        boolean result = LoggingLevelValidator.isProductionSafe(root, security, oauth2, sql);

        // Then
        assertThat(result).isTrue();
    }
}
