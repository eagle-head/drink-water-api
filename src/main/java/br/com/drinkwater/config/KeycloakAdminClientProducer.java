package br.com.drinkwater.config;

import br.com.drinkwater.config.properties.KeycloakProperties;
import java.util.concurrent.TimeUnit;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!it-no-containers")
public class KeycloakAdminClientProducer {

    private final KeycloakProperties keycloakProperties;

    public KeycloakAdminClientProducer(KeycloakProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
    }

    @Bean
    Keycloak configKeycloak() {
        var adminClient = keycloakProperties.adminClient();
        return KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.url())
                .realm(keycloakProperties.realm())
                .clientId(keycloakProperties.clientId())
                .username(keycloakProperties.username())
                .password(keycloakProperties.password())
                .resteasyClient(
                        new ResteasyClientBuilderImpl()
                                .connectTimeout(
                                        adminClient.connectTimeout().toSeconds(), TimeUnit.SECONDS)
                                .readTimeout(
                                        adminClient.readTimeout().toSeconds(), TimeUnit.SECONDS)
                                .connectionPoolSize(adminClient.connectionPoolSize())
                                .build())
                .build();
    }
}
