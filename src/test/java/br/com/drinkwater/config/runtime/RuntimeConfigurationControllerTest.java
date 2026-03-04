package br.com.drinkwater.config.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = RuntimeConfigurationController.class)
@ActiveProfiles("test")
@Import(br.com.drinkwater.config.TestMessageSourceConfig.class)
final class RuntimeConfigurationControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RuntimeConfigurationService configurationService;

    @MockitoBean private RuntimeConfigurationValidator configurationValidator;

    private static final String ADMIN_SCOPE = "SCOPE_drinkwater:v1:admin:config:manage";
    private static final String READ_SCOPE = "SCOPE_drinkwater:v1:admin:config:read";

    @Test
    void givenAdminScope_whenUpdateLogLevel_thenReturnsSuccess() throws Exception {
        var request =
                new RuntimeConfigurationController.LogLevelUpdateRequest(
                        "br.com.drinkwater", "DEBUG");

        mockMvc.perform(
                        post("/management/runtime-config/logging/level")
                                .with(jwt().authorities(new SimpleGrantedAuthority(ADMIN_SCOPE)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.logger").value("br.com.drinkwater"))
                .andExpect(jsonPath("$.level").value("DEBUG"));

        verify(configurationService).updateLogLevel("br.com.drinkwater", "DEBUG");
    }

    @Test
    void givenAdminScope_whenRefreshConfiguration_thenReturnsSuccess() throws Exception {
        mockMvc.perform(
                        post("/management/runtime-config/refresh")
                                .with(jwt().authorities(new SimpleGrantedAuthority(ADMIN_SCOPE))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Runtime configuration refresh triggered"));

        verify(configurationService).refreshConfiguration();
    }

    @Test
    void givenReadScope_whenGetConfigurationSummary_thenReturnsSummary() throws Exception {
        var summary =
                Map.of(
                        "logging", Map.of("root", "INFO", "app", "DEBUG"),
                        "monitoring", Map.of("prometheusStep", "15s"),
                        "actuator", Map.of("healthShowDetails", "never"),
                        "lastUpdated", "2025-01-01T00:00:00Z");
        when(configurationService.getConfigurationSummary()).thenReturn(summary);

        mockMvc.perform(
                        get("/management/runtime-config/summary")
                                .with(jwt().authorities(new SimpleGrantedAuthority(READ_SCOPE))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logging.root").value("INFO"))
                .andExpect(jsonPath("$.logging.app").value("DEBUG"));

        verify(configurationService).getConfigurationSummary();
    }

    @Test
    void givenReadScope_whenGetLoggingConfiguration_thenReturnsLoggingConfig() throws Exception {
        var loggingConfig = Map.of("root", "INFO", "app", "DEBUG");
        var summary =
                Map.of(
                        "logging",
                        loggingConfig,
                        "monitoring",
                        Map.of(),
                        "actuator",
                        Map.of(),
                        "lastUpdated",
                        "2025-01-01T00:00:00Z");
        when(configurationService.getConfigurationSummary()).thenReturn(summary);

        mockMvc.perform(
                        get("/management/runtime-config/logging")
                                .with(jwt().authorities(new SimpleGrantedAuthority(READ_SCOPE))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.logging.root").value("INFO"));

        verify(configurationService).getConfigurationSummary();
    }

    @Test
    void givenValidConfiguration_whenValidateConfiguration_thenReturnsSuccess() throws Exception {
        var result =
                new RuntimeConfigurationValidator.ValidationResult(
                        true, "Runtime configuration is valid", java.util.List.of());
        when(configurationValidator.validateManually()).thenReturn(result);

        mockMvc.perform(
                        post("/management/runtime-config/validate")
                                .with(jwt().authorities(new SimpleGrantedAuthority(ADMIN_SCOPE))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Runtime configuration is valid"));

        verify(configurationValidator).validateManually();
    }

    @Test
    void givenInvalidConfiguration_whenValidateConfiguration_thenReturnsBadRequest()
            throws Exception {
        var result =
                new RuntimeConfigurationValidator.ValidationResult(
                        false,
                        "Runtime configuration validation failed",
                        java.util.List.of("Error 1"));
        when(configurationValidator.validateManually()).thenReturn(result);

        mockMvc.perform(
                        post("/management/runtime-config/validate")
                                .with(jwt().authorities(new SimpleGrantedAuthority(ADMIN_SCOPE))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.errors").isArray());

        verify(configurationValidator).validateManually();
    }

    @Test
    void givenBlankLoggerName_whenUpdateLogLevel_thenReturnsBadRequest() throws Exception {
        var request = new RuntimeConfigurationController.LogLevelUpdateRequest("", "DEBUG");

        mockMvc.perform(
                        post("/management/runtime-config/logging/level")
                                .with(jwt().authorities(new SimpleGrantedAuthority(ADMIN_SCOPE)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenInvalidLogLevel_whenUpdateLogLevel_thenReturnsBadRequest() throws Exception {
        var request =
                new RuntimeConfigurationController.LogLevelUpdateRequest(
                        "br.com.drinkwater", "INVALID");

        mockMvc.perform(
                        post("/management/runtime-config/logging/level")
                                .with(jwt().authorities(new SimpleGrantedAuthority(ADMIN_SCOPE)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAdminRole_whenUpdateLogLevel_thenReturnsSuccess() throws Exception {
        var request = new RuntimeConfigurationController.LogLevelUpdateRequest("ROOT", "INFO");

        mockMvc.perform(
                        post("/management/runtime-config/logging/level")
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(configurationService).updateLogLevel("ROOT", "INFO");
    }

    @Test
    void logLevelUpdateRequest_recordAccessors() {
        var request =
                new RuntimeConfigurationController.LogLevelUpdateRequest("testLogger", "WARN");
        assertThat(request.loggerName()).isEqualTo("testLogger");
        assertThat(request.level()).isEqualTo("WARN");
    }
}
