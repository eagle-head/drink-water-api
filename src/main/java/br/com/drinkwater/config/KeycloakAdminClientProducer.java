package br.com.drinkwater.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminClientProducer {

    @Bean
    public Keycloak configKeycloak(
            @Value("${keycloak.url}") String serverURL,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.clientId}") String clientId,
            @Value("${keycloak.username}") String username,
            @Value("${keycloak.password}") String password,
            @Value("${keycloak.clientSecret}") String clientSecret
    ) {

        return KeycloakBuilder.builder()
                .serverUrl(serverURL)
                .clientSecret(clientSecret)
                .realm(realm)
                .clientId(clientId)
                .username(username)
                .password(password)
                .build();
    }
}