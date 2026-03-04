package br.com.drinkwater.config;

import static br.com.drinkwater.support.MockTestAuthProvider.JOHN_DOE_TOKEN;
import static br.com.drinkwater.support.MockTestAuthProvider.NEW_USER_TOKEN;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

/**
 * Mock configuration for integration tests when Docker/Testcontainers are not available. This
 * configuration provides mocked JWT decoder and uses H2 database instead of PostgreSQL.
 */
@TestConfiguration(proxyBeanMethods = false)
@Profile("it-no-containers")
public class MockContainersConfig {

    /**
     * Mock JwtDecoder that returns predefined JWT tokens for testing. This allows integration tests
     * to work without a real Keycloak instance.
     */
    private static final String ALL_SCOPES =
            "openid "
                    + "drinkwater:v1:user:profile:read "
                    + "drinkwater:v1:user:profile:create "
                    + "drinkwater:v1:user:profile:update "
                    + "drinkwater:v1:user:profile:delete "
                    + "drinkwater:v1:waterintake:entry:read "
                    + "drinkwater:v1:waterintake:entry:create "
                    + "drinkwater:v1:waterintake:entry:update "
                    + "drinkwater:v1:waterintake:entry:delete "
                    + "drinkwater:v1:waterintake:entries:search";

    @Bean
    @Primary
    JwtDecoder mockJwtDecoder() throws Exception {
        JwtDecoder decoder = org.mockito.Mockito.mock(JwtDecoder.class);

        Jwt johnDoeJwt =
                Jwt.withTokenValue(JOHN_DOE_TOKEN)
                        .header("alg", "RS256")
                        .subject("fbc58717-5d48-4041-9f1c-257e8052428f")
                        .claim("email", "john.doe@test.com")
                        .claim("preferred_username", "john.doe@test.com")
                        .claim("given_name", "John")
                        .claim("family_name", "Doe")
                        .claim("aud", "drinkwaterapp")
                        .claim("iss", "http://localhost:8080/realms/drinkwater")
                        .claim("scope", ALL_SCOPES)
                        .claim("realm_access", Map.of("roles", List.of("default-roles-drinkwater")))
                        .issuedAt(Instant.now().minusSeconds(60))
                        .expiresAt(Instant.now().plusSeconds(3600))
                        .build();

        Jwt newUserJwt =
                Jwt.withTokenValue(NEW_USER_TOKEN)
                        .header("alg", "RS256")
                        .subject("550e8400-e29b-41d4-a716-446655440000")
                        .claim("email", "new.user@test.com")
                        .claim("preferred_username", "new.user@test.com")
                        .claim("given_name", "New")
                        .claim("family_name", "User")
                        .claim("aud", "drinkwaterapp")
                        .claim("iss", "http://localhost:8080/realms/drinkwater")
                        .claim("scope", ALL_SCOPES)
                        .claim("realm_access", Map.of("roles", List.of("default-roles-drinkwater")))
                        .issuedAt(Instant.now().minusSeconds(60))
                        .expiresAt(Instant.now().plusSeconds(3600))
                        .build();

        // Configure mock to return appropriate JWT based on token value
        when(decoder.decode(anyString()))
                .thenAnswer(
                        invocation -> {
                            String token = invocation.getArgument(0);
                            if (JOHN_DOE_TOKEN.equals(token)) {
                                return johnDoeJwt;
                            } else if (NEW_USER_TOKEN.equals(token)) {
                                return newUserJwt;
                            }
                            return johnDoeJwt;
                        });

        return decoder;
    }
}
