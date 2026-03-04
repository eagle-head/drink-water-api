package br.com.drinkwater.config.keycloak;

/**
 * Thrown when an operation against the Keycloak Admin Client fails after all resilience attempts
 * (retries and circuit breaker) have been exhausted.
 */
public class KeycloakOperationException extends RuntimeException {

    public KeycloakOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
