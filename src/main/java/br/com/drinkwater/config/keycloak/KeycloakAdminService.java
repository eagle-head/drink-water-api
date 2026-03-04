package br.com.drinkwater.config.keycloak;

import br.com.drinkwater.config.properties.KeycloakProperties;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.UUID;
import org.keycloak.admin.client.Keycloak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!it-no-containers")
public class KeycloakAdminService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakAdminService.class);

    private final Keycloak keycloak;
    private final KeycloakProperties keycloakProperties;

    public KeycloakAdminService(Keycloak keycloak, KeycloakProperties keycloakProperties) {
        this.keycloak = keycloak;
        this.keycloakProperties = keycloakProperties;
    }

    @Retry(name = "keycloak")
    @CircuitBreaker(name = "keycloak", fallbackMethod = "deleteUserFallback")
    public void deleteUser(UUID publicId) {
        keycloak.realm(keycloakProperties.realm()).users().delete(publicId.toString());
        log.info("User {} deleted from Keycloak", publicId);
    }

    void deleteUserFallback(UUID publicId, Exception e) {
        log.error(
                "Failed to delete user {} from Keycloak after retries: {}",
                publicId,
                e.getMessage());
        throw new KeycloakOperationException(
                "Keycloak is unavailable. User deleted locally but not in identity provider.", e);
    }
}
