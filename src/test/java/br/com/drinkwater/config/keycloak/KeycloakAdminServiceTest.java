package br.com.drinkwater.config.keycloak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.drinkwater.config.properties.KeycloakProperties;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
final class KeycloakAdminServiceTest {

    private static final UUID PUBLIC_ID = UUID.fromString("fbc58717-5d48-4041-9f1c-257e8052428f");

    @Mock private Keycloak keycloak;
    @Mock private KeycloakProperties keycloakProperties;

    private KeycloakAdminService keycloakAdminService;

    @BeforeEach
    void setUp() {
        keycloakAdminService = new KeycloakAdminService(keycloak, keycloakProperties);
    }

    @Test
    void givenKeycloakAvailable_whenDeleteUser_thenDelegatesToKeycloak() {
        // Given
        when(keycloakProperties.realm()).thenReturn("drinkwater");
        RealmResource realmResource = mock(RealmResource.class);
        UsersResource usersResource = mock(UsersResource.class);
        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);

        // When
        keycloakAdminService.deleteUser(PUBLIC_ID);

        // Then
        verify(keycloak).realm("drinkwater");
        verify(realmResource).users();
        verify(usersResource).delete(PUBLIC_ID.toString());
    }

    @Test
    void givenKeycloakThrowsException_whenDeleteUser_thenExceptionPropagates() {
        // Given
        when(keycloakProperties.realm()).thenReturn("drinkwater");
        when(keycloak.realm(anyString()))
                .thenThrow(new jakarta.ws.rs.ProcessingException("timeout"));

        // When / Then
        assertThatThrownBy(() -> keycloakAdminService.deleteUser(PUBLIC_ID))
                .isInstanceOf(jakarta.ws.rs.ProcessingException.class)
                .hasMessageContaining("timeout");
    }

    @Test
    void givenPublicId_whenDeleteUser_thenPassesCorrectIdAsString() {
        // Given
        when(keycloakProperties.realm()).thenReturn("test-realm");
        RealmResource realmResource = mock(RealmResource.class);
        UsersResource usersResource = mock(UsersResource.class);
        when(keycloak.realm("test-realm")).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);

        // When
        keycloakAdminService.deleteUser(PUBLIC_ID);

        // Then
        verify(usersResource).delete("fbc58717-5d48-4041-9f1c-257e8052428f");
    }

    @Test
    void givenException_whenDeleteUserFallback_thenThrowsKeycloakOperationException() {
        var cause = new IOException("connection refused");

        assertThatThrownBy(() -> keycloakAdminService.deleteUserFallback(PUBLIC_ID, cause))
                .isInstanceOf(KeycloakOperationException.class)
                .hasMessageContaining("Keycloak is unavailable")
                .hasCause(cause);
    }

    @Test
    void givenException_whenDeleteUserFallback_thenExceptionContainsPublicIdContext() {
        var cause = new RuntimeException("timeout");

        assertThatThrownBy(() -> keycloakAdminService.deleteUserFallback(PUBLIC_ID, cause))
                .isInstanceOf(KeycloakOperationException.class)
                .satisfies(ex -> assertThat(ex.getCause()).hasMessageContaining("timeout"));
    }
}
