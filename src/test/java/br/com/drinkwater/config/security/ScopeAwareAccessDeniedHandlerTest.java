package br.com.drinkwater.config.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.authorization.AuthorizationDeniedException;

final class ScopeAwareAccessDeniedHandlerTest {

    private final ScopeAwareAccessDeniedHandler handler = new ScopeAwareAccessDeniedHandler();

    @Test
    void givenDenialWithScopeAuthority_whenExtractMissingScope_thenReturnScope() {
        var ex =
                new AuthorizationDeniedException(
                        "Access Denied: "
                                + "hasAuthority('SCOPE_drinkwater:v1:user:profile:read')");

        Optional<String> result = handler.extractMissingScope(ex);

        assertThat(result).hasValue("drinkwater:v1:user:profile:read");
    }

    @Test
    void givenDenialWithWaterIntakeScope_whenExtractMissingScope_thenReturnScope() {
        var ex =
                new AuthorizationDeniedException(
                        "Access Denied: "
                                + "hasAuthority('SCOPE_drinkwater:v1:waterintake:entry:create')");

        Optional<String> result = handler.extractMissingScope(ex);

        assertThat(result).hasValue("drinkwater:v1:waterintake:entry:create");
    }

    @Test
    void givenDenialWithoutScope_whenExtractMissingScope_thenReturnEmpty() {
        var ex = new AuthorizationDeniedException("Access Denied: hasRole('ADMIN')");

        Optional<String> result = handler.extractMissingScope(ex);

        assertThat(result).isEmpty();
    }

    @Test
    void givenDenialWithGenericMessage_whenExtractMissingScope_thenReturnEmpty() {
        var ex = new AuthorizationDeniedException("Access Denied");

        Optional<String> result = handler.extractMissingScope(ex);

        assertThat(result).isEmpty();
    }
}
