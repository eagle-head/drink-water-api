package br.com.drinkwater.config.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Status;

@ExtendWith(MockitoExtension.class)
final class KeycloakHealthIndicatorTest {

    private static final String WELL_KNOWN_URL =
            "http://localhost:8080/realms/drinkwater/.well-known/openid-configuration";

    @Mock private KeycloakHealthClient keycloakHealthClient;

    private KeycloakHealthIndicator healthIndicator;

    @BeforeEach
    void setUp() {
        when(keycloakHealthClient.getWellKnownUrl()).thenReturn(WELL_KNOWN_URL);
        healthIndicator = new KeycloakHealthIndicator(keycloakHealthClient);
    }

    @Test
    void givenKeycloakReturns200_whenHealth_thenReturnsUp() throws Exception {
        // Given
        when(keycloakHealthClient.checkKeycloak()).thenReturn(200);

        // When
        var health = healthIndicator.health();

        // Then
        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsEntry("url", WELL_KNOWN_URL);
        assertThat(health.getDetails()).containsEntry("status", 200);
    }

    @Test
    void givenKeycloakReturns503_whenHealth_thenReturnsDown() throws Exception {
        // Given
        when(keycloakHealthClient.checkKeycloak()).thenReturn(503);

        // When
        var health = healthIndicator.health();

        // Then
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsEntry("url", WELL_KNOWN_URL);
        assertThat(health.getDetails()).containsEntry("status", 503);
    }

    @Test
    void givenFallbackTriggered_whenHealth_thenReturnsDownWithFallbackDetail() throws Exception {
        // Given
        when(keycloakHealthClient.checkKeycloak())
                .thenReturn(KeycloakHealthClient.FALLBACK_STATUS_CODE);

        // When
        var health = healthIndicator.health();

        // Then
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsEntry("url", WELL_KNOWN_URL);
        assertThat(health.getDetails()).containsEntry("fallback", true);
    }

    @Test
    void givenConnectionRefused_whenHealth_thenReturnsDown() throws Exception {
        // Given
        when(keycloakHealthClient.checkKeycloak()).thenThrow(new IOException("Connection refused"));

        // When
        var health = healthIndicator.health();

        // Then
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsEntry("url", WELL_KNOWN_URL);
        assertThat(health.getDetails()).containsKey("error");
    }

    @Test
    void givenInterrupted_whenHealth_thenReturnsDownAndPreservesInterruptFlag() throws Exception {
        // Given
        when(keycloakHealthClient.checkKeycloak())
                .thenThrow(new InterruptedException("interrupted"));

        // When
        var health = healthIndicator.health();

        // Then
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsEntry("url", WELL_KNOWN_URL);
        assertThat(Thread.currentThread().isInterrupted()).isTrue();

        Thread.interrupted();
    }
}
