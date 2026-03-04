package br.com.drinkwater.config;

import br.com.drinkwater.config.security.AuthenticatedUser;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    private final String appVersion;
    private final String baseUrl;

    public OpenApiConfig(
            @Value("${app.version}") String appVersion, @Value("${cors.baseUrl}") String baseUrl) {
        this.appVersion = appVersion;
        this.baseUrl = baseUrl;
    }

    @Bean
    public OpenAPI drinkWaterOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Drink Water API")
                                .version(appVersion)
                                .description("REST API for hydration tracking and user management")
                                .license(
                                        new License()
                                                .name("MIT")
                                                .url("https://opensource.org/licenses/MIT")))
                .addServersItem(new Server().url(baseUrl).description("Current environment"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        SECURITY_SCHEME_NAME,
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")));
    }

    @Bean
    public ParameterCustomizer authenticatedUserParameterCustomizer() {
        return (parameterModel, methodParameter) -> {
            if (methodParameter.hasParameterAnnotation(AuthenticatedUser.class)) {
                return null;
            }
            return parameterModel;
        };
    }
}
