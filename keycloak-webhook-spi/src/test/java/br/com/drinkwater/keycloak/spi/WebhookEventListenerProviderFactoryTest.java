package br.com.drinkwater.keycloak.spi;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;

class WebhookEventListenerProviderFactoryTest {

    @Test
    void givenFactory_whenGetId_thenReturnsExpectedId() {
        var factory = new WebhookEventListenerProviderFactory();
        assertEquals("webhook-event-listener", factory.getId());
    }

    @Test
    void givenConfiguredFactory_whenCreate_thenReturnsProviderInstance() {
        var factory = new WebhookEventListenerProviderFactory();

        var config = mock(Config.Scope.class);
        when(config.get("webhookUrl")).thenReturn("http://localhost:8081/internal/webhooks/keycloak");
        when(config.get("webhookSecret", "")).thenReturn("test-secret");

        factory.init(config);

        var session = mock(KeycloakSession.class);
        var provider = factory.create(session);

        assertNotNull(provider);
        assertInstanceOf(WebhookEventListenerProvider.class, provider);
    }

    @Test
    void givenFactory_whenCloseAndPostInit_thenDoesNotThrow() {
        var factory = new WebhookEventListenerProviderFactory();

        var config = mock(Config.Scope.class);
        when(config.get("webhookUrl")).thenReturn("http://test");
        when(config.get("webhookSecret", "")).thenReturn("");
        factory.init(config);

        assertDoesNotThrow(() -> factory.postInit(null));
        assertDoesNotThrow(factory::close);
    }
}
