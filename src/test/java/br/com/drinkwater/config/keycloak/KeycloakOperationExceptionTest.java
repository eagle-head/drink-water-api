package br.com.drinkwater.config.keycloak;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class KeycloakOperationExceptionTest {

    @Test
    void givenMessageAndCause_whenCreated_thenBothArePreserved() {
        // Given
        String message = "Keycloak unavailable";
        Throwable cause = new RuntimeException("connection timeout");

        // When
        var exception = new KeycloakOperationException(message, cause);

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void givenException_thenIsRuntimeException() {
        var exception = new KeycloakOperationException("test", new RuntimeException("cause"));
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
