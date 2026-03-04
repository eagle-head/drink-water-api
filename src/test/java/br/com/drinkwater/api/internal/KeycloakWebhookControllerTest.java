package br.com.drinkwater.api.internal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.drinkwater.config.SecurityConfig;
import br.com.drinkwater.config.TestMessageSourceConfig;
import br.com.drinkwater.config.properties.ActuatorProperties;
import br.com.drinkwater.config.properties.WebhookProperties;
import br.com.drinkwater.usermanagement.service.UserService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = KeycloakWebhookController.class)
@ActiveProfiles("test")
@Import({TestMessageSourceConfig.class, SecurityConfig.class})
final class KeycloakWebhookControllerTest {

    private static final String WEBHOOK_PATH = "/internal/webhooks/keycloak";
    private static final String SECRET_HEADER = "X-Webhook-Secret";
    private static final String VALID_SECRET = "test-webhook-secret";
    private static final UUID USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    @Autowired private MockMvc mockMvc;

    @MockitoBean private UserService userService;

    @MockitoBean private WebhookProperties webhookProperties;

    @MockitoBean private ActuatorProperties actuatorProperties;

    @BeforeEach
    void setUp() {
        when(actuatorProperties.endpoints()).thenReturn(List.of("health"));
        when(actuatorProperties.basePath()).thenReturn("/actuator");
        when(webhookProperties.secret()).thenReturn(VALID_SECRET);
    }

    @Test
    void givenValidSecretAndDeleteAccountEvent_whenHandleKeycloakEvent_thenReturns200()
            throws Exception {
        String json =
                """
                {
                    "eventType": "DELETE_ACCOUNT",
                    "userId": "%s",
                    "realmId": "drinkwater",
                    "timestamp": 1234567890
                }
                """
                        .formatted(USER_ID);

        mockMvc.perform(
                        post(WEBHOOK_PATH)
                                .header(SECRET_HEADER, VALID_SECRET)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isOk());

        verify(userService).deleteByPublicId(USER_ID);
    }

    @Test
    void givenValidSecretAndAdminDeleteEvent_whenHandleKeycloakEvent_thenReturns200()
            throws Exception {
        String json =
                """
                {
                    "eventType": "ADMIN_DELETE_USER",
                    "userId": "%s",
                    "realmId": "drinkwater",
                    "timestamp": 1234567890
                }
                """
                        .formatted(USER_ID);

        mockMvc.perform(
                        post(WEBHOOK_PATH)
                                .header(SECRET_HEADER, VALID_SECRET)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isOk());

        verify(userService).deleteByPublicId(USER_ID);
    }

    @Test
    void givenInvalidSecret_whenHandleKeycloakEvent_thenReturns401() throws Exception {
        String json =
                """
                {
                    "eventType": "DELETE_ACCOUNT",
                    "userId": "%s",
                    "realmId": "drinkwater",
                    "timestamp": 1234567890
                }
                """
                        .formatted(USER_ID);

        mockMvc.perform(
                        post(WEBHOOK_PATH)
                                .header(SECRET_HEADER, "wrong-secret")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService);
    }

    @Test
    void givenMissingSecret_whenHandleKeycloakEvent_thenReturns401() throws Exception {
        String json =
                """
                {
                    "eventType": "DELETE_ACCOUNT",
                    "userId": "%s",
                    "realmId": "drinkwater",
                    "timestamp": 1234567890
                }
                """
                        .formatted(USER_ID);

        mockMvc.perform(post(WEBHOOK_PATH).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService);
    }

    @Test
    void givenInvalidUuidFormat_whenHandleKeycloakEvent_thenReturns400() throws Exception {
        String json =
                """
                {
                    "eventType": "DELETE_ACCOUNT",
                    "userId": "not-a-valid-uuid",
                    "realmId": "drinkwater",
                    "timestamp": 1234567890
                }
                """;

        mockMvc.perform(
                        post(WEBHOOK_PATH)
                                .header(SECRET_HEADER, VALID_SECRET)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void givenMissingEventType_whenHandleKeycloakEvent_thenReturns400() throws Exception {
        String json =
                """
                {
                    "userId": "%s",
                    "realmId": "drinkwater",
                    "timestamp": 1234567890
                }
                """
                        .formatted(USER_ID);

        mockMvc.perform(
                        post(WEBHOOK_PATH)
                                .header(SECRET_HEADER, VALID_SECRET)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }
}
