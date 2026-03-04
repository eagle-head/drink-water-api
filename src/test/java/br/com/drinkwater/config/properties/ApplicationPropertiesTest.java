package br.com.drinkwater.config.properties;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Tests for ApplicationProperties record to ensure environment detection (isProduction,
 * isDevelopment, isStaging) works for all aliases.
 */
final class ApplicationPropertiesTest {

    @Test
    void givenEnvironmentProduction_whenIsProduction_thenReturnsTrue() {
        // Given
        ApplicationProperties properties = new ApplicationProperties("app", "1.0.0", "production");

        // When
        boolean result = properties.isProduction();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenEnvironmentProd_whenIsProduction_thenReturnsTrue() {
        // Given
        ApplicationProperties properties = new ApplicationProperties("app", "1.0.0", "prod");

        // When
        boolean result = properties.isProduction();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenEnvironmentDevelopment_whenIsDevelopment_thenReturnsTrue() {
        // Given
        ApplicationProperties properties = new ApplicationProperties("app", "1.0.0", "development");

        // When
        boolean result = properties.isDevelopment();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenEnvironmentDev_whenIsDevelopment_thenReturnsTrue() {
        // Given
        ApplicationProperties properties = new ApplicationProperties("app", "1.0.0", "dev");

        // When
        boolean result = properties.isDevelopment();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenEnvironmentStaging_whenIsStaging_thenReturnsTrue() {
        // Given
        ApplicationProperties properties = new ApplicationProperties("app", "1.0.0", "staging");

        // When
        boolean result = properties.isStaging();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenEnvironmentStage_whenIsStaging_thenReturnsTrue() {
        // Given
        ApplicationProperties properties = new ApplicationProperties("app", "1.0.0", "stage");

        // When
        boolean result = properties.isStaging();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenNonMatchingEnvironment_whenIsProductionIsDevelopmentIsStaging_thenAllReturnFalse() {
        // Given
        ApplicationProperties properties = new ApplicationProperties("app", "1.0.0", "custom");

        // When
        boolean isProduction = properties.isProduction();
        boolean isDevelopment = properties.isDevelopment();
        boolean isStaging = properties.isStaging();

        // Then
        assertThat(isProduction).isFalse();
        assertThat(isDevelopment).isFalse();
        assertThat(isStaging).isFalse();
    }
}
