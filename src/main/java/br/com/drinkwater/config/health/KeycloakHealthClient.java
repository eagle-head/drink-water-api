package br.com.drinkwater.config.health;

import br.com.drinkwater.config.properties.KeycloakProperties;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!it-no-containers")
public class KeycloakHealthClient {

    private static final Logger log = LoggerFactory.getLogger(KeycloakHealthClient.class);
    static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);
    static final int FALLBACK_STATUS_CODE = -1;

    private final String wellKnownUrl;
    private final HttpClient httpClient;

    public KeycloakHealthClient(KeycloakProperties keycloakProperties, HttpClient httpClient) {
        this.wellKnownUrl =
                keycloakProperties.url()
                        + "/realms/"
                        + keycloakProperties.realm()
                        + "/.well-known/openid-configuration";
        this.httpClient = httpClient;
    }

    @Retry(name = "keycloak")
    @CircuitBreaker(name = "keycloak", fallbackMethod = "healthFallback")
    public int checkKeycloak() throws IOException, InterruptedException {
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(wellKnownUrl))
                        .timeout(REQUEST_TIMEOUT)
                        .GET()
                        .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
    }

    int healthFallback(Exception e) {
        log.warn("Keycloak health check fallback triggered: {}", e.getMessage());
        return FALLBACK_STATUS_CODE;
    }

    public String getWellKnownUrl() {
        return wellKnownUrl;
    }
}
