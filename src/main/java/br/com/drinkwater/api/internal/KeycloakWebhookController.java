package br.com.drinkwater.api.internal;

import br.com.drinkwater.api.internal.dto.KeycloakEventDTO;
import br.com.drinkwater.usermanagement.service.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Internal webhook controller for Keycloak identity provider events.
 *
 * <p>Handles user lifecycle events sent by Keycloak's webhook event listener SPI at {@code
 * /internal/webhooks/keycloak}. Currently supports user deletion events to synchronize local user
 * data cleanup. This endpoint is secured by a shared webhook secret, not by OAuth2 tokens.
 */
@RestController
@RequestMapping("/internal/webhooks")
public class KeycloakWebhookController {

    private static final Logger log = LoggerFactory.getLogger(KeycloakWebhookController.class);

    private final UserService userService;

    public KeycloakWebhookController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Receives user deletion events from Keycloak's webhook event listener SPI. The operation is
     * idempotent: if the user does not exist locally, the delete is a no-op.
     */
    @PostMapping("/keycloak")
    public ResponseEntity<Void> handleKeycloakEvent(@Valid @RequestBody KeycloakEventDTO event) {
        log.info(
                "Keycloak webhook received: eventType={}, userId={}, realmId={}",
                event.eventType(),
                event.userId(),
                event.realmId());

        UUID publicId;
        try {
            publicId = UUID.fromString(event.userId());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid userId format in webhook: {}", event.userId());
            return ResponseEntity.badRequest().build();
        }

        this.userService.deleteByPublicId(publicId);
        log.info("User data cleanup completed for publicId={}", publicId);

        return ResponseEntity.ok().build();
    }
}
