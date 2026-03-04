package br.com.drinkwater.keycloak.spi;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.logging.Logger;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

/**
 * Factory for {@link WebhookEventListenerProvider}. The factory is a singleton per Keycloak server;
 * individual provider instances are created per request via {@link #create(KeycloakSession)}.
 *
 * <p>Configuration is read from Keycloak's SPI config during {@link #init(Config.Scope)}:
 *
 * <pre>
 *   spi-events-listener--webhook-event-listener--webhook-url=http://...
 *   spi-events-listener--webhook-event-listener--webhook-secret=secret
 * </pre>
 *
 * Or via environment variables:
 *
 * <pre>
 *   KC_SPI_EVENTS_LISTENER__WEBHOOK_EVENT_LISTENER__WEBHOOK_URL=http://...
 *   KC_SPI_EVENTS_LISTENER__WEBHOOK_EVENT_LISTENER__WEBHOOK_SECRET=secret
 * </pre>
 */
public class WebhookEventListenerProviderFactory implements EventListenerProviderFactory {

    private static final Logger LOG =
            Logger.getLogger(WebhookEventListenerProviderFactory.class.getName());

    static final String PROVIDER_ID = "webhook-event-listener";

    private HttpClient httpClient;
    private String webhookUrl;
    private String webhookSecret;

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new WebhookEventListenerProvider(session, httpClient, webhookUrl, webhookSecret);
    }

    @Override
    public void init(Config.Scope config) {
        webhookUrl = config.get("webhookUrl");
        webhookSecret = config.get("webhookSecret", "");

        if (webhookUrl == null || webhookUrl.isBlank()) {
            LOG.warning(
                    "Webhook URL not configured — webhook-event-listener will be inactive. "
                            + "Set KC_SPI_EVENTS_LISTENER__WEBHOOK_EVENT_LISTENER__WEBHOOK_URL");
        } else {
            LOG.info("Webhook event listener configured: url=" + webhookUrl);
        }

        httpClient =
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(5))
                        .followRedirects(HttpClient.Redirect.NEVER)
                        .build();
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // No post-initialization needed
    }

    @Override
    public void close() {
        // HttpClient does not require explicit close in JDK 17+
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
