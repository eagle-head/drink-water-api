package br.com.drinkwater.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Mock configuration for integration tests when Docker/Testcontainers are not available.
 * This configuration provides mocked JWT decoder and uses H2 database instead of PostgreSQL.
 */
@TestConfiguration(proxyBeanMethods = false)
@Profile("it-no-containers")
public class MockContainersConfig {

    /**
     * Mock JwtDecoder that returns predefined JWT tokens for testing.
     * This allows integration tests to work without a real Keycloak instance.
     */
    @Bean
    @Primary
    public JwtDecoder mockJwtDecoder() throws Exception {
        JwtDecoder decoder = org.mockito.Mockito.mock(JwtDecoder.class);
        
        // Mock JWT for John Doe user
        Jwt johnDoeJwt = Jwt.withTokenValue("mock-jwt-john-doe")
            .header("alg", "RS256")
            .subject("fbc58717-5d48-4041-9f1c-257e8052428f") // matches data.sql
            .claim("email", "john.doe@test.com")
            .claim("preferred_username", "john.doe@test.com")
            .claim("given_name", "John")
            .claim("family_name", "Doe")
            .claim("aud", "drinkwaterapp")
            .claim("iss", "http://localhost:8080/realms/drinkwater")
            .issuedAt(Instant.now().minusSeconds(60))
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
            
        // Mock JWT for new user
        Jwt newUserJwt = Jwt.withTokenValue("mock-jwt-new-user")
            .header("alg", "RS256")
            .subject("550e8400-e29b-41d4-a716-446655440000") // new user UUID
            .claim("email", "new.user@test.com")
            .claim("preferred_username", "new.user@test.com")
            .claim("given_name", "New")
            .claim("family_name", "User")
            .claim("aud", "drinkwaterapp")
            .claim("iss", "http://localhost:8080/realms/drinkwater")
            .issuedAt(Instant.now().minusSeconds(60))
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
        
        // Configure mock to return appropriate JWT based on token value
        when(decoder.decode(anyString())).thenAnswer(invocation -> {
            String token = invocation.getArgument(0);
            if (token.contains("john-doe")) {
                return johnDoeJwt;
            } else if (token.contains("new-user")) {
                return newUserJwt;
            }
            // Default return for any other token
            return johnDoeJwt;
        });
        
        return decoder;
    }
}