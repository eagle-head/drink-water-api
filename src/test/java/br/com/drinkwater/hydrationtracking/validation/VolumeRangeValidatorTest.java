package br.com.drinkwater.hydrationtracking.validation;

import static br.com.drinkwater.usermanagement.constants.UserTestConstants.USER_UUID;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.drinkwater.api.v1.controller.WaterIntakeControllerV1;
import br.com.drinkwater.config.TestMessageSourceConfig;
import br.com.drinkwater.hydrationtracking.service.WaterIntakeService;
import br.com.drinkwater.usermanagement.service.UserService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = WaterIntakeControllerV1.class)
@ActiveProfiles("test")
@Import(TestMessageSourceConfig.class)
final class VolumeRangeValidatorTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private MessageSource messageSource;

    @MockitoBean private WaterIntakeService waterIntakeService;

    @MockitoBean private UserService userService;

    @Test
    void givenMaxVolumeLessThanMinVolume_whenSearch_thenReturnBadRequest() throws Exception {
        var startDate = Instant.now().minus(7, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
        var endDate = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        var locale = Locale.of("en", "US");
        var expectedDetail = messageSource.getMessage("validation.error", null, locale);
        var expectedMessage =
                messageSource.getMessage("water-intake.filter.volume-range.invalid", null, locale);

        mockMvc.perform(
                        get("/api/v1/users/water-intakes")
                                .with(
                                        jwt().jwt(
                                                        builder ->
                                                                builder.claim(
                                                                        "sub",
                                                                        USER_UUID.toString()))
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "SCOPE_drinkwater:v1:waterintake:entries:search")))
                                .param("startDate", startDate.toString())
                                .param("endDate", endDate.toString())
                                .param("minVolume", "500")
                                .param("maxVolume", "100")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.type").value("https://www.drinkwater.com.br/validation-error"))
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.detail").value(expectedDetail))
                .andExpect(jsonPath("$.instance").value("/api/v1/users/water-intakes"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.field=='maxVolume')]").exists())
                .andExpect(
                        jsonPath("$.errors[?(@.field=='maxVolume')].message")
                                .value(expectedMessage));

        verifyNoInteractions(waterIntakeService);
    }

    @Test
    void givenMaxVolumeEqualToMinVolume_whenSearch_thenDoNotReturnVolumeRangeError()
            throws Exception {
        var startDate = Instant.now().minus(7, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
        var endDate = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        mockMvc.perform(
                        get("/api/v1/users/water-intakes")
                                .with(
                                        jwt().jwt(
                                                        builder ->
                                                                builder.claim(
                                                                        "sub",
                                                                        USER_UUID.toString()))
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "SCOPE_drinkwater:v1:waterintake:entries:search")))
                                .param("startDate", startDate.toString())
                                .param("endDate", endDate.toString())
                                .param("minVolume", "500")
                                .param("maxVolume", "500")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(jsonPath("$.errors[?(@.field=='maxVolume')]").doesNotExist());
    }

    @Test
    void givenMaxVolumeGreaterThanMinVolume_whenSearch_thenDoNotReturnVolumeRangeError()
            throws Exception {
        var startDate = Instant.now().minus(7, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
        var endDate = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        mockMvc.perform(
                        get("/api/v1/users/water-intakes")
                                .with(
                                        jwt().jwt(
                                                        builder ->
                                                                builder.claim(
                                                                        "sub",
                                                                        USER_UUID.toString()))
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "SCOPE_drinkwater:v1:waterintake:entries:search")))
                                .param("startDate", startDate.toString())
                                .param("endDate", endDate.toString())
                                .param("minVolume", "100")
                                .param("maxVolume", "500")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(jsonPath("$.errors[?(@.field=='maxVolume')]").doesNotExist());
    }
}
