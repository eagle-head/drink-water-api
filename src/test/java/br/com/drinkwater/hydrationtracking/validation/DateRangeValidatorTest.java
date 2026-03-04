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
final class DateRangeValidatorTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private MessageSource messageSource;

    @MockitoBean private WaterIntakeService waterIntakeService;

    @MockitoBean private UserService userService;

    @Test
    void givenEndDateBeforeStartDate_whenSearch_thenReturnBadRequest() throws Exception {
        var startDate = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        var endDate = startDate.minus(1, ChronoUnit.DAYS);
        var locale = Locale.of("en", "US");
        var expectedDetail = messageSource.getMessage("validation.error", null, locale);
        var expectedMessage =
                messageSource.getMessage(
                        "water-intake.filter.date-range.end-before-start", null, locale);

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
                .andExpect(jsonPath("$.errors[?(@.field=='endDate')]").exists())
                .andExpect(
                        jsonPath("$.errors[?(@.field=='endDate')].message").value(expectedMessage));

        verifyNoInteractions(waterIntakeService);
    }

    @Test
    void givenEndDateEqualToStartDate_whenSearch_thenDoNotReturnDateRangeError() throws Exception {
        var date = Instant.now().minus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS);

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
                                .param("startDate", date.toString())
                                .param("endDate", date.toString())
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(jsonPath("$.errors[?(@.field=='endDate')]").doesNotExist());
    }
}
