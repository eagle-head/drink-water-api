package br.com.drinkwater.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import br.com.drinkwater.config.properties.ActuatorProperties;
import br.com.drinkwater.config.properties.WebhookProperties;
import br.com.drinkwater.config.security.KeycloakJwtAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final ActuatorProperties actuatorProperties;
    private final WebhookProperties webhookProperties;
    private final KeycloakJwtAuthenticationConverter jwtAuthenticationConverter;

    public SecurityConfig(
            ActuatorProperties actuatorProperties,
            WebhookProperties webhookProperties,
            KeycloakJwtAuthenticationConverter jwtAuthenticationConverter) {
        this.actuatorProperties = actuatorProperties;
        this.webhookProperties = webhookProperties;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        String[] actuatorEndpoints =
                actuatorProperties.endpoints().stream()
                        .map(endpoint -> actuatorProperties.basePath() + "/" + endpoint)
                        .toArray(String[]::new);

        return http.authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers(actuatorEndpoints)
                                        .permitAll()
                                        .requestMatchers("/internal/**")
                                        .permitAll()
                                        .requestMatchers(
                                                "/v3/api-docs/**",
                                                "/swagger-ui/**",
                                                "/swagger-ui.html")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .addFilterBefore(
                        new WebhookSecurityFilter(webhookProperties),
                        UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .oauth2ResourceServer(
                        oauth2 ->
                                oauth2.jwt(
                                        jwt ->
                                                jwt.jwtAuthenticationConverter(
                                                        jwtAuthenticationConverter)))
                .build();
    }
}
