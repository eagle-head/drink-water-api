package br.com.drinkwater.usermanagement.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    private final static String POSTGRES_IMAGE = "postgres:16-alpine";
    private final static String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:26.0.7";
    private final static String realmImportFile = "/keycloak-realms.json";
    private final static String realmName = "drinkwater";

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgres() {
        return new PostgreSQLContainer<>(POSTGRES_IMAGE)
                .withDatabaseName("drink_water_db")
                .withUsername("username")
                .withPassword("password");
    }

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public KeycloakContainer keycloak(DynamicPropertyRegistry registry) {
        var keycloak = new KeycloakContainer(KEYCLOAK_IMAGE)
                .withBootstrapAdminDisabled()
                .withRealmImportFile(realmImportFile)
                .withAdminUsername("admin")
                .withAdminPassword("password");

        registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloak.getAuthServerUrl() + "/realms/" + realmName
        );

        registry.add(
                "spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                () -> keycloak.getAuthServerUrl() + "/realms/" + realmName + "/protocol/openid-connect/certs"
        );

        return keycloak;
    }
}
