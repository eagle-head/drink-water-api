package br.com.drinkwater.keycloak.spi;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.http.HttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.events.Event;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakTransactionManager;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WebhookEventListenerProviderTest {

    @Mock private KeycloakSession session;
    @Mock private KeycloakTransactionManager transactionManager;
    @Mock private HttpClient httpClient;

    private WebhookEventListenerProvider provider;

    @BeforeEach
    void setUp() {
        provider =
                new WebhookEventListenerProvider(
                        session, httpClient, "http://localhost:8081/internal/webhooks/keycloak", "test-secret");
    }

    @Test
    void givenDeleteAccountEvent_whenOnEvent_thenEnqueuesWebhook() {
        when(session.getTransactionManager()).thenReturn(transactionManager);

        var event = new Event();
        event.setType(EventType.DELETE_ACCOUNT);
        event.setUserId("550e8400-e29b-41d4-a716-446655440000");
        event.setRealmId("drinkwater");
        event.setTime(1234567890L);

        provider.onEvent(event);

        verify(transactionManager).enlistAfterCompletion(any());
    }

    @Test
    void givenLoginEvent_whenOnEvent_thenDoesNotEnqueueWebhook() {
        var event = new Event();
        event.setType(EventType.LOGIN);
        event.setUserId("some-user-id");

        provider.onEvent(event);

        verifyNoInteractions(session);
    }

    @Test
    void givenDeleteAccountEventWithNullUserId_whenOnEvent_thenSkips() {
        var event = new Event();
        event.setType(EventType.DELETE_ACCOUNT);
        event.setUserId(null);

        provider.onEvent(event);

        verifyNoInteractions(session);
    }

    @Test
    void givenAdminDeleteUserEvent_whenOnEvent_thenEnqueuesWebhook() {
        when(session.getTransactionManager()).thenReturn(transactionManager);

        var adminEvent = new AdminEvent();
        adminEvent.setOperationType(OperationType.DELETE);
        adminEvent.setResourceType(ResourceType.USER);
        adminEvent.setResourcePath("users/550e8400-e29b-41d4-a716-446655440000");
        adminEvent.setRealmId("drinkwater");
        adminEvent.setTime(1234567890L);

        provider.onEvent(adminEvent, false);

        verify(transactionManager).enlistAfterCompletion(any());
    }

    @Test
    void givenAdminDeleteClientEvent_whenOnEvent_thenDoesNotEnqueueWebhook() {
        var adminEvent = new AdminEvent();
        adminEvent.setOperationType(OperationType.DELETE);
        adminEvent.setResourceType(ResourceType.CLIENT);
        adminEvent.setResourcePath("clients/some-client-id");

        provider.onEvent(adminEvent, false);

        verifyNoInteractions(session);
    }

    @Test
    void givenAdminUpdateUserEvent_whenOnEvent_thenDoesNotEnqueueWebhook() {
        var adminEvent = new AdminEvent();
        adminEvent.setOperationType(OperationType.UPDATE);
        adminEvent.setResourceType(ResourceType.USER);
        adminEvent.setResourcePath("users/some-user-id");

        provider.onEvent(adminEvent, false);

        verifyNoInteractions(session);
    }

    @Test
    void givenAdminDeleteUserWithInvalidPath_whenOnEvent_thenSkips() {
        var adminEvent = new AdminEvent();
        adminEvent.setOperationType(OperationType.DELETE);
        adminEvent.setResourceType(ResourceType.USER);
        adminEvent.setResourcePath("groups/some-id");

        provider.onEvent(adminEvent, false);

        verifyNoInteractions(session);
    }

    @Test
    void givenValidUsersPath_whenExtractUserId_thenReturnsId() {
        String result = provider.extractUserIdFromResourcePath("users/abc-123");
        assertEquals("abc-123", result);
    }

    @Test
    void givenNullPath_whenExtractUserId_thenReturnsNull() {
        assertNull(provider.extractUserIdFromResourcePath(null));
    }

    @Test
    void givenEmptyUsersPath_whenExtractUserId_thenReturnsNull() {
        assertNull(provider.extractUserIdFromResourcePath("users/"));
    }

    @Test
    void givenNonUsersPath_whenExtractUserId_thenReturnsNull() {
        assertNull(provider.extractUserIdFromResourcePath("clients/abc-123"));
    }
}
