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
 * Testcontainers configuration for full integration tests. Requires Docker to be running. Use
 * 'it-no-containers' profile for environments without Docker.
 */
@TestConfiguration(proxyBeanMethods = false)
@Profile("!it-no-containers")
public class ContainersConfig {

    private static final String POSTGRES_IMAGE = "postgres:16-alpine";
    private static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:26.5.4";
    private static final String realmImportFile = "/keycloak-realms.json";
    private static final String realmName = "drinkwater";

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgres() {
        var container = new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE));
        container.withDatabaseName("drink_water_db");
        container.withUsername("username");
        container.withPassword("password");
        container.withReuse(true);
        return container;
    }

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    KeycloakContainer keycloak(DynamicPropertyRegistry registry) {
        var keycloak = new KeycloakContainer(KEYCLOAK_IMAGE);
        keycloak.withBootstrapAdminDisabled();
        keycloak.withRealmImportFile(realmImportFile);
        keycloak.withAdminUsername("admin");
        keycloak.withAdminPassword("password");
        keycloak.withReuse(true);

        // Register dynamic properties for OAuth2 configuration
        registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloak.getAuthServerUrl() + "/realms/" + realmName);

        registry.add(
                "spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                () ->
                        keycloak.getAuthServerUrl()
                                + "/realms/"
                                + realmName
                                + "/protocol/openid-connect/certs");

        registry.add("keycloak.url", keycloak::getAuthServerUrl);

        registry.add(
                "keycloak.issuerUri", () -> keycloak.getAuthServerUrl() + "/realms/" + realmName);

        registry.add(
                "keycloak.jwkSetUri",
                () ->
                        keycloak.getAuthServerUrl()
                                + "/realms/"
                                + realmName
                                + "/protocol/openid-connect/certs");

        return keycloak;
    }
}
