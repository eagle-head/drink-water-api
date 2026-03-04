package br.com.drinkwater.config;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.drinkwater.config.security.AuthenticatedUser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springframework.core.MethodParameter;

final class OpenApiConfigTest {

    private static final String APP_VERSION = "1.0.0-TEST";
    private static final String BASE_URL = "http://localhost:8081";

    private OpenApiConfig config;
    private OpenAPI openAPI;
    private ParameterCustomizer parameterCustomizer;

    @BeforeEach
    void setUp() {
        config = new OpenApiConfig(APP_VERSION, BASE_URL);
        openAPI = config.drinkWaterOpenAPI();
        parameterCustomizer = config.authenticatedUserParameterCustomizer();
    }

    @Test
    void openApiShouldHaveCorrectTitle() {
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Drink Water API");
    }

    @Test
    void openApiShouldHaveCorrectVersion() {
        assertThat(openAPI.getInfo().getVersion()).isEqualTo(APP_VERSION);
    }

    @Test
    void openApiShouldHaveMitLicense() {
        assertThat(openAPI.getInfo().getLicense().getName()).isEqualTo("MIT");
        assertThat(openAPI.getInfo().getLicense().getUrl())
                .isEqualTo("https://opensource.org/licenses/MIT");
    }

    @Test
    void openApiShouldHaveDescription() {
        assertThat(openAPI.getInfo().getDescription()).isNotBlank();
    }

    @Test
    void openApiShouldHaveServerUrl() {
        assertThat(openAPI.getServers()).hasSize(1);
        assertThat(openAPI.getServers().getFirst().getUrl()).isEqualTo(BASE_URL);
        assertThat(openAPI.getServers().getFirst().getDescription())
                .isEqualTo("Current environment");
    }

    @Test
    void openApiShouldHaveBearerSecurityScheme() {
        var scheme = openAPI.getComponents().getSecuritySchemes().get("bearerAuth");
        assertThat(scheme).isNotNull();
        assertThat(scheme.getType().toString()).isEqualTo("http");
        assertThat(scheme.getScheme()).isEqualTo("bearer");
        assertThat(scheme.getBearerFormat()).isEqualTo("JWT");
    }

    @Test
    void openApiShouldHaveGlobalSecurityRequirement() {
        assertThat(openAPI.getSecurity()).hasSize(1);
        assertThat(openAPI.getSecurity().getFirst()).containsKey("bearerAuth");
    }

    @Test
    void parameterCustomizerShouldRemoveAuthenticatedUserParameter() throws NoSuchMethodException {
        var method = SampleController.class.getMethod("withAnnotation", UUID.class);
        var methodParameter = new MethodParameter(method, 0);

        var result = parameterCustomizer.customize(new Parameter(), methodParameter);
        assertThat(result).isNull();
    }

    @Test
    void parameterCustomizerShouldPreserveNonAuthenticatedUserParameter()
            throws NoSuchMethodException {
        var method = SampleController.class.getMethod("withoutAnnotation", String.class);
        var methodParameter = new MethodParameter(method, 0);

        var param = new Parameter();
        var result = parameterCustomizer.customize(param, methodParameter);
        assertThat(result).isSameAs(param);
    }

    static class SampleController {
        public void withAnnotation(@AuthenticatedUser UUID publicId) {}

        public void withoutAnnotation(String name) {}
    }
}
