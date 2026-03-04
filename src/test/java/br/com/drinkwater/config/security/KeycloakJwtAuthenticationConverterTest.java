package br.com.drinkwater.config.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

final class KeycloakJwtAuthenticationConverterTest {

    private final KeycloakJwtAuthenticationConverter converter =
            new KeycloakJwtAuthenticationConverter();

    @Test
    void givenJwtWithScopes_whenConvert_thenAuthoritiesContainScopePrefixedEntries() {
        Jwt jwt =
                Jwt.withTokenValue("token")
                        .header("alg", "RS256")
                        .subject("user-id")
                        .claim("scope", "openid drinkwater:v1:user:profile:read")
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(300))
                        .build();

        AbstractAuthenticationToken result = converter.convert(jwt);

        List<String> authorities =
                result.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        assertThat(authorities).contains("SCOPE_openid", "SCOPE_drinkwater:v1:user:profile:read");
    }

    @Test
    void givenJwtWithRealmRoles_whenConvert_thenAuthoritiesContainRolePrefixedEntries() {
        Jwt jwt =
                Jwt.withTokenValue("token")
                        .header("alg", "RS256")
                        .subject("user-id")
                        .claim("realm_access", Map.of("roles", List.of("ADMIN", "offline_access")))
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(300))
                        .build();

        AbstractAuthenticationToken result = converter.convert(jwt);

        List<String> authorities =
                result.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        assertThat(authorities).contains("ROLE_ADMIN", "ROLE_offline_access");
    }

    @Test
    void givenJwtWithScopesAndRoles_whenConvert_thenAuthoritiesCombineBoth() {
        Jwt jwt =
                Jwt.withTokenValue("token")
                        .header("alg", "RS256")
                        .subject("user-id")
                        .claim("scope", "drinkwater:v1:waterintake:entry:read")
                        .claim("realm_access", Map.of("roles", List.of("ADMIN")))
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(300))
                        .build();

        AbstractAuthenticationToken result = converter.convert(jwt);

        List<String> authorities =
                result.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        assertThat(authorities)
                .contains("SCOPE_drinkwater:v1:waterintake:entry:read", "ROLE_ADMIN");
    }

    @Test
    void givenJwtWithoutScopesOrRoles_whenConvert_thenAuthoritiesAreEmpty() {
        Jwt jwt =
                Jwt.withTokenValue("token")
                        .header("alg", "RS256")
                        .subject("user-id")
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(300))
                        .build();

        AbstractAuthenticationToken result = converter.convert(jwt);

        assertThat(result.getAuthorities()).isEmpty();
    }

    @Test
    void givenJwtWithRealmAccessButNoRolesKey_whenConvert_thenNoRoleAuthorities() {
        Jwt jwt =
                Jwt.withTokenValue("token")
                        .header("alg", "RS256")
                        .subject("user-id")
                        .claim("realm_access", Map.of("other", "value"))
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(300))
                        .build();

        AbstractAuthenticationToken result = converter.convert(jwt);

        List<String> authorities =
                result.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        assertThat(authorities).noneMatch(a -> a.startsWith("ROLE_"));
    }
}
