package br.com.drinkwater.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Testcontainers configuration for full integration tests.
 * Requires Docker to be running. Use 'it-no-containers' profile for environments without Docker.
 */
@TestConfiguration(proxyBeanMethods = false)
@Profile("!it-no-containers")
public class ContainersConfig {

    private final static String POSTGRES_IMAGE = "postgres:16-alpine";
    private final static String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:26.0.3"; // Use stable version that matches project
    private final static String realmImportFile = "/keycloak-realms.json";
    private final static String realmName = "drinkwater";

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgres() {
        return new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE))
                .withDatabaseName("drink_water_db")
                .withUsername("username")
                .withPassword("password")
                .withReuse(true); // Enable container reuse for faster test execution
    }

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public KeycloakContainer keycloak(DynamicPropertyRegistry registry) {
        var keycloak = new KeycloakContainer(KEYCLOAK_IMAGE)
                .withBootstrapAdminDisabled()
                .withRealmImportFile(realmImportFile)
                .withAdminUsername("admin")
                .withAdminPassword("password")
                .withReuse(true); // Enable container reuse for faster test execution

        // Register dynamic properties for OAuth2 configuration
        registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloak.getAuthServerUrl() + "/realms/" + realmName
        );

        registry.add(
                "spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                () -> keycloak.getAuthServerUrl() + "/realms/" + realmName + "/protocol/openid-connect/certs"
        );

        // Also register for TestAuthProvider compatibility
        registry.add(
                "keycloak.url",
                keycloak::getAuthServerUrl
        );

        return keycloak;
    }
}
