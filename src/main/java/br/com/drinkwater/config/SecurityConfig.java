package br.com.drinkwater.config;

import br.com.drinkwater.config.properties.ActuatorProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final ActuatorProperties actuatorProperties;

    public SecurityConfig(ActuatorProperties actuatorProperties) {
        this.actuatorProperties = actuatorProperties;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Build actuator endpoint paths dynamically from configuration
        String[] actuatorEndpoints = actuatorProperties.endpoints().stream()
                .map(endpoint -> actuatorProperties.basePath() + "/" + endpoint)
                .toArray(String[]::new);

        return http
                .authorizeHttpRequests(authorize -> authorize
                        // SECURITY CONFIGURATION: Actuator endpoints are controlled by environment variables.
                        // Production environments should limit endpoints to: health, info, metrics, prometheus
                        // and set ACTUATOR_HEALTH_SHOW_DETAILS=never for security.
                        .requestMatchers(actuatorEndpoints).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }
}
