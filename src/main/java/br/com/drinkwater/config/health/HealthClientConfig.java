package br.com.drinkwater.config.health;

import java.net.http.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!it-no-containers")
class HealthClientConfig {

    @Bean
    HttpClient healthCheckHttpClient() {
        return HttpClient.newBuilder().connectTimeout(KeycloakHealthClient.REQUEST_TIMEOUT).build();
    }
}
