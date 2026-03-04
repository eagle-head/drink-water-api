package br.com.drinkwater.config.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.com.drinkwater.config.properties.KeycloakProperties;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
final class KeycloakHealthClientTest {

    private static final String KEYCLOAK_URL = "http://localhost:8080";
    private static final String KEYCLOAK_REALM = "drinkwater";
    private static final String EXPECTED_WELL_KNOWN_URL =
            KEYCLOAK_URL + "/realms/" + KEYCLOAK_REALM + "/.well-known/openid-configuration";

    @Mock private HttpClient httpClient;

    @Mock private HttpResponse<Void> httpResponse;

    private KeycloakHealthClient healthClient;

    @BeforeEach
    void setUp() {
        var keycloakProperties =
                new KeycloakProperties(
                        KEYCLOAK_URL,
                        KEYCLOAK_REALM,
                        "client-id",
                        "username",
                        "password1234",
                        KEYCLOAK_URL + "/realms/" + KEYCLOAK_REALM,
                        KEYCLOAK_URL
                                + "/realms/"
                                + KEYCLOAK_REALM
                                + "/protocol/openid-connect/certs",
                        null);
        healthClient = new KeycloakHealthClient(keycloakProperties, httpClient);
    }

    @Test
    void givenKeycloakReturns200_whenCheckKeycloak_thenReturns200() throws Exception {
        // Given
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpClient.send(
                        any(HttpRequest.class),
                        ArgumentMatchers.<HttpResponse.BodyHandler<Void>>any()))
                .thenReturn(httpResponse);

        // When
        int statusCode = healthClient.checkKeycloak();

        // Then
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    void givenKeycloakReturns503_whenCheckKeycloak_thenReturns503() throws Exception {
        // Given
        when(httpResponse.statusCode()).thenReturn(503);
        when(httpClient.send(
                        any(HttpRequest.class),
                        ArgumentMatchers.<HttpResponse.BodyHandler<Void>>any()))
                .thenReturn(httpResponse);

        // When
        int statusCode = healthClient.checkKeycloak();

        // Then
        assertThat(statusCode).isEqualTo(503);
    }

    @Test
    void givenConnectionFails_whenCheckKeycloak_thenThrowsIOException() throws Exception {
        // Given
        when(httpClient.send(
                        any(HttpRequest.class),
                        ArgumentMatchers.<HttpResponse.BodyHandler<Void>>any()))
                .thenThrow(new IOException("Connection refused"));

        // When / Then
        org.junit.jupiter.api.Assertions.assertThrows(
                IOException.class, () -> healthClient.checkKeycloak());
    }

    @Test
    void givenValidConfig_whenGetWellKnownUrl_thenReturnsExpectedUrl() {
        assertThat(healthClient.getWellKnownUrl()).isEqualTo(EXPECTED_WELL_KNOWN_URL);
    }

    @Test
    void givenFallbackStatusCode_thenEqualsMinusOne() {
        assertThat(KeycloakHealthClient.FALLBACK_STATUS_CODE).isEqualTo(-1);
    }

    @Test
    void givenException_whenHealthFallback_thenReturnsFallbackStatusCode() {
        var exception = new IOException("connection refused");

        int result = healthClient.healthFallback(exception);

        assertThat(result).isEqualTo(KeycloakHealthClient.FALLBACK_STATUS_CODE);
    }

    @Test
    void givenRuntimeException_whenHealthFallback_thenReturnsFallbackStatusCode() {
        var exception = new RuntimeException("unexpected error");

        int result = healthClient.healthFallback(exception);

        assertThat(result).isEqualTo(KeycloakHealthClient.FALLBACK_STATUS_CODE);
    }
}
