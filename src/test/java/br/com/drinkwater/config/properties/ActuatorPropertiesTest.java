package br.com.drinkwater.config.properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for ActuatorProperties record to ensure endpoint validation, isProductionSafe,
 * hasDevelopmentEndpoints, and getEndpointsAsString behave correctly.
 */
final class ActuatorPropertiesTest {

    @Test
    void givenValidEndpoints_whenConstruct_thenSucceeds() {
        // Given
        List<String> endpoints = List.of("health", "info", "metrics", "prometheus");

        // When
        ActuatorProperties properties =
                new ActuatorProperties(endpoints, "/actuator", "never", "never");

        // Then
        assertThat(properties.endpoints()).isEqualTo(endpoints);
    }

    @Test
    void givenNullEndpoints_whenConstruct_thenSucceedsWithoutValidation() {
        // When
        ActuatorProperties properties = new ActuatorProperties(null, "/actuator", "never", "never");

        // Then
        assertThat(properties.endpoints()).isNull();
    }

    @Test
    void givenInvalidEndpoint_whenConstruct_thenThrowsIllegalArgumentException() {
        // Given
        List<String> endpoints = List.of("health", "invalid-endpoint");

        // When & Then
        assertThatThrownBy(() -> new ActuatorProperties(endpoints, "/actuator", "never", "never"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid actuator endpoint")
                .hasMessageContaining("invalid-endpoint");
    }

    @Test
    void givenProductionSafeEndpointsAndNeverShowDetails_whenIsProductionSafe_thenReturnsTrue() {
        // Given
        ActuatorProperties properties =
                new ActuatorProperties(
                        List.of("health", "info", "metrics", "prometheus"),
                        "/actuator",
                        "never",
                        "never");

        // When
        boolean result = properties.isProductionSafe();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenDevelopmentEndpoints_whenIsProductionSafe_thenReturnsFalse() {
        // Given
        ActuatorProperties properties =
                new ActuatorProperties(
                        List.of("health", "beans", "env"), "/actuator", "never", "never");

        // When
        boolean result = properties.isProductionSafe();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void givenWhenAuthorizedHealthShowDetails_whenIsProductionSafe_thenReturnsFalse() {
        // Given
        ActuatorProperties properties =
                new ActuatorProperties(
                        List.of("health", "info"), "/actuator", "when-authorized", "never");

        // When
        boolean result = properties.isProductionSafe();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void givenDevelopmentEndpoints_whenHasDevelopmentEndpoints_thenReturnsTrue() {
        // Given
        ActuatorProperties properties =
                new ActuatorProperties(
                        List.of("health", "beans", "env"), "/actuator", "never", "never");

        // When
        boolean result = properties.hasDevelopmentEndpoints();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenOnlyProductionEndpoints_whenHasDevelopmentEndpoints_thenReturnsFalse() {
        // Given
        ActuatorProperties properties =
                new ActuatorProperties(
                        List.of("health", "info", "metrics", "prometheus"),
                        "/actuator",
                        "never",
                        "never");

        // When
        boolean result = properties.hasDevelopmentEndpoints();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void givenMultipleEndpoints_whenGetEndpointsAsString_thenReturnsCommaSeparated() {
        // Given
        ActuatorProperties properties =
                new ActuatorProperties(
                        List.of("health", "info", "metrics"), "/actuator", "never", "never");

        // When
        String result = properties.getEndpointsAsString();

        // Then
        assertThat(result).isEqualTo("health,info,metrics");
    }
}
