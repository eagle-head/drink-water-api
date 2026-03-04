package br.com.drinkwater.keycloak.spi;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.AbstractKeycloakTransaction;
import org.keycloak.models.KeycloakSession;

/**
 * Keycloak Event Listener that forwards user deletion events to the Drink Water API via HTTP
 * webhook.
 *
 * <p>Handles two deletion scenarios:
 *
 * <ul>
 *   <li>{@link EventType#DELETE_ACCOUNT} — user self-service deletion via Account Console
 *   <li>{@link OperationType#DELETE} on {@link ResourceType#USER} — admin deletion via Admin
 *       Console or REST API
 * </ul>
 *
 * <p>The HTTP call is enqueued via {@link
 * org.keycloak.models.KeycloakTransactionManager#enlistAfterCompletion} so it only fires after the
 * Keycloak transaction commits successfully, as recommended by the {@link EventListenerProvider}
 * contract.
 */
public class WebhookEventListenerProvider implements EventListenerProvider {

    private static final Logger LOG =
            Logger.getLogger(WebhookEventListenerProvider.class.getName());

    private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(10);
    private static final String USERS_PATH_PREFIX = "users/";

    private final KeycloakSession session;
    private final HttpClient httpClient;
    private final String webhookUrl;
    private final String webhookSecret;

    public WebhookEventListenerProvider(
            KeycloakSession session,
            HttpClient httpClient,
            String webhookUrl,
            String webhookSecret) {
        this.session = session;
        this.httpClient = httpClient;
        this.webhookUrl = webhookUrl;
        this.webhookSecret = webhookSecret;
    }

    @Override
    public void onEvent(Event event) {
        if (event.getType() != EventType.DELETE_ACCOUNT) {
            return;
        }

        String userId = event.getUserId();
        if (userId == null || userId.isBlank()) {
            LOG.warning("DELETE_ACCOUNT event received with null/blank userId, skipping");
            return;
        }

        enqueueWebhook("DELETE_ACCOUNT", userId, event.getRealmId(), event.getTime());
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        if (event.getOperationType() != OperationType.DELETE
                || event.getResourceType() != ResourceType.USER) {
            return;
        }

        String userId = extractUserIdFromResourcePath(event.getResourcePath());
        if (userId == null) {
            LOG.warning(
                    "Admin DELETE USER event with unparseable resourcePath: "
                            + event.getResourcePath());
            return;
        }

        enqueueWebhook("ADMIN_DELETE_USER", userId, event.getRealmId(), event.getTime());
    }

    @Override
    public void close() {
        // Provider is per-request; HttpClient is managed by the factory
    }

    /**
     * Extracts the user ID from an admin event resource path.
     *
     * @param resourcePath path like "users/550e8400-e29b-41d4-a716-446655440000"
     * @return the user ID, or null if the path cannot be parsed
     */
    String extractUserIdFromResourcePath(String resourcePath) {
        if (resourcePath == null || !resourcePath.startsWith(USERS_PATH_PREFIX)) {
            return null;
        }
        String userId = resourcePath.substring(USERS_PATH_PREFIX.length());
        return userId.isBlank() ? null : userId;
    }

    private void enqueueWebhook(String eventType, String userId, String realmId, long timestamp) {
        LOG.info(
                "Enqueuing webhook: eventType="
                        + eventType
                        + ", userId="
                        + userId
                        + ", realmId="
                        + realmId);

        session.getTransactionManager()
                .enlistAfterCompletion(
                        new AbstractKeycloakTransaction() {
                            @Override
                            protected void commitImpl() {
                                sendWebhook(eventType, userId, realmId, timestamp);
                            }

                            @Override
                            protected void rollbackImpl() {
                                LOG.info(
                                        "Transaction rolled back, webhook not sent for userId="
                                                + userId);
                            }
                        });
    }

    private void sendWebhook(String eventType, String userId, String realmId, long timestamp) {
        String json =
                "{\"eventType\":\""
                        + eventType
                        + "\",\"userId\":\""
                        + userId
                        + "\",\"realmId\":\""
                        + realmId
                        + "\",\"timestamp\":"
                        + timestamp
                        + "}";

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(webhookUrl))
                        .header("Content-Type", "application/json")
                        .header("X-Webhook-Secret", webhookSecret)
                        .timeout(HTTP_TIMEOUT)
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

        try {
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                LOG.info(
                        "Webhook dispatched: eventType="
                                + eventType
                                + ", userId="
                                + userId
                                + ", status="
                                + response.statusCode());
            } else {
                LOG.log(
                        Level.SEVERE,
                        "Webhook failed: eventType={0}, userId={1}, status={2}, body={3}",
                        new Object[] {
                            eventType, userId, response.statusCode(), response.body()
                        });
            }
        } catch (IOException e) {
            LOG.log(
                    Level.SEVERE,
                    "Webhook I/O error: eventType="
                            + eventType
                            + ", userId="
                            + userId
                            + ", url="
                            + webhookUrl,
                    e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.log(
                    Level.SEVERE,
                    "Webhook interrupted: eventType="
                            + eventType
                            + ", userId="
                            + userId,
                    e);
        }
    }
}
