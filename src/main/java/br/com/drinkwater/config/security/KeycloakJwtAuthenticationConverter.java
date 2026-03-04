package br.com.drinkwater.config.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

/**
 * Converts a Keycloak-issued JWT into an {@link AbstractAuthenticationToken} that combines both
 * OAuth2 scopes (from the {@code scope} claim) and Keycloak realm roles (from the {@code
 * realm_access.roles} claim).
 *
 * <p>Scopes are prefixed with {@code SCOPE_} by Spring Security's default converter. Realm roles
 * are prefixed with {@code ROLE_} to work with {@code hasRole()} expressions.
 */
@Component
public class KeycloakJwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String ROLES_KEY = "roles";
    private static final String ROLE_PREFIX = "ROLE_";

    private final JwtGrantedAuthoritiesConverter scopeConverter;

    public KeycloakJwtAuthenticationConverter() {
        this.scopeConverter = new JwtGrantedAuthoritiesConverter();
    }

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> scopeAuthorities = scopeConverter.convert(jwt);
        Collection<GrantedAuthority> roleAuthorities = extractRealmRoles(jwt);

        List<GrantedAuthority> combined =
                Stream.concat(scopeAuthorities.stream(), roleAuthorities.stream()).toList();

        return new JwtAuthenticationToken(jwt, combined);
    }

    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS_CLAIM);
        if (realmAccess == null) {
            return Collections.emptyList();
        }

        Object rolesObj = realmAccess.get(ROLES_KEY);
        if (!(rolesObj instanceof Collection<?> roles)) {
            return Collections.emptyList();
        }

        return ((Collection<String>) roles)
                .stream()
                        .map(
                                role ->
                                        (GrantedAuthority)
                                                new SimpleGrantedAuthority(ROLE_PREFIX + role))
                        .toList();
    }
}
