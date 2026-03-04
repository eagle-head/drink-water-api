package br.com.drinkwater.config.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!it-no-containers")
public class KeycloakHealthIndicator implements HealthIndicator {

    private static final Logger log = LoggerFactory.getLogger(KeycloakHealthIndicator.class);

    private final KeycloakHealthClient keycloakHealthClient;

    public KeycloakHealthIndicator(KeycloakHealthClient keycloakHealthClient) {
        this.keycloakHealthClient = keycloakHealthClient;
    }

    @Override
    public Health health() {
        try {
            int statusCode = keycloakHealthClient.checkKeycloak();

            if (statusCode == KeycloakHealthClient.FALLBACK_STATUS_CODE) {
                log.warn("Keycloak health check returned fallback status");
                return Health.down()
                        .withDetail("url", keycloakHealthClient.getWellKnownUrl())
                        .withDetail("fallback", true)
                        .build();
            }

            if (statusCode == 200) {
                return Health.up()
                        .withDetail("url", keycloakHealthClient.getWellKnownUrl())
                        .withDetail("status", statusCode)
                        .build();
            }

            log.warn("Keycloak returned non-200 status: {}", statusCode);
            return Health.down()
                    .withDetail("url", keycloakHealthClient.getWellKnownUrl())
                    .withDetail("status", statusCode)
                    .build();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Keycloak health check interrupted", e);
            return Health.down()
                    .withDetail("url", keycloakHealthClient.getWellKnownUrl())
                    .withException(e)
                    .build();
        } catch (Exception e) {
            log.error("Keycloak health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("url", keycloakHealthClient.getWellKnownUrl())
                    .withException(e)
                    .build();
        }
    }
}
