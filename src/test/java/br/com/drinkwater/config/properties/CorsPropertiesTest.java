package br.com.drinkwater.config.properties;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for CorsProperties record to ensure compact constructor fallback, isBaseUrlAllowed,
 * areOriginsSecure, and getPrimaryOrigin behave correctly.
 */
final class CorsPropertiesTest {

    @Test
    void
            givenAllowedOriginsNullAndAllowedOriginSet_whenConstruct_thenAllowedOriginsFallsBackToSplit() {
        // Given
        String allowedOrigin = "https://a.com,https://b.com";

        // When
        CorsProperties properties =
                new CorsProperties(
                        "https://example.com",
                        allowedOrigin,
                        null,
                        List.of("GET", "POST"),
                        List.of("*"),
                        true,
                        3600L);

        // Then
        assertThat(properties.allowedOrigins()).containsExactly("https://a.com", "https://b.com");
    }

    @Test
    void givenBothAllowedOriginsAndAllowedOriginNull_whenConstruct_thenAllowedOriginsRemainsNull() {
        // When
        CorsProperties properties =
                new CorsProperties(
                        "https://example.com",
                        null,
                        null,
                        List.of("GET", "POST"),
                        List.of("*"),
                        true,
                        3600L);

        // Then
        assertThat(properties.allowedOrigins()).isNull();
    }

    @Test
    void givenBaseUrlInAllowedOrigins_whenIsBaseUrlAllowed_thenReturnsTrue() {
        // Given
        String baseUrl = "https://example.com";
        CorsProperties properties =
                new CorsProperties(
                        baseUrl,
                        baseUrl,
                        List.of(baseUrl, "https://other.com"),
                        List.of("GET"),
                        List.of("*"),
                        true,
                        3600L);

        // When
        boolean result = properties.isBaseUrlAllowed();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenBaseUrlNotInAllowedOrigins_whenIsBaseUrlAllowed_thenReturnsFalse() {
        // Given
        CorsProperties properties =
                new CorsProperties(
                        "https://example.com",
                        "https://other.com",
                        List.of("https://other.com"),
                        List.of("GET"),
                        List.of("*"),
                        true,
                        3600L);

        // When
        boolean result = properties.isBaseUrlAllowed();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void givenAllOriginsUseHttps_whenAreOriginsSecure_thenReturnsTrue() {
        // Given
        CorsProperties properties =
                new CorsProperties(
                        "https://example.com",
                        "https://example.com",
                        List.of("https://example.com", "https://other.com"),
                        List.of("GET"),
                        List.of("*"),
                        true,
                        3600L);

        // When
        boolean result = properties.areOriginsSecure();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenOneOriginUsesHttp_whenAreOriginsSecure_thenReturnsFalse() {
        // Given
        CorsProperties properties =
                new CorsProperties(
                        "https://example.com",
                        "https://example.com",
                        List.of("https://example.com", "http://insecure.com"),
                        List.of("GET"),
                        List.of("*"),
                        true,
                        3600L);

        // When
        boolean result = properties.areOriginsSecure();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void givenNonEmptyAllowedOrigins_whenGetPrimaryOrigin_thenReturnsFirstInList() {
        // Given
        CorsProperties properties =
                new CorsProperties(
                        "https://example.com",
                        "https://fallback.com",
                        List.of("https://primary.com", "https://secondary.com"),
                        List.of("GET"),
                        List.of("*"),
                        true,
                        3600L);

        // When
        String result = properties.getPrimaryOrigin();

        // Then
        assertThat(result).isEqualTo("https://primary.com");
    }

    @Test
    void givenEmptyAllowedOrigins_whenGetPrimaryOrigin_thenReturnsAllowedOrigin() {
        // Given
        String allowedOrigin = "https://fallback.com";
        CorsProperties properties =
                new CorsProperties(
                        "https://example.com",
                        allowedOrigin,
                        List.of(),
                        List.of("GET"),
                        List.of("*"),
                        true,
                        3600L);

        // When
        String result = properties.getPrimaryOrigin();

        // Then
        assertThat(result).isEqualTo(allowedOrigin);
    }
}
