package br.com.drinkwater.api.internal.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO representing an event received from Keycloak's webhook event listener SPI. The {@code
 * eventType} identifies the lifecycle event (e.g. user deletion), {@code userId} is the Keycloak
 * user UUID, {@code realmId} identifies the realm, and {@code timestamp} is the event epoch millis.
 */
public record KeycloakEventDTO(
        @NotBlank String eventType,
        @NotBlank String userId,
        @NotBlank String realmId,
        long timestamp) {}
