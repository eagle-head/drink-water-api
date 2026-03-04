package br.com.drinkwater.usermanagement.validation;

import static br.com.drinkwater.usermanagement.constants.UserTestConstants.USER_UUID;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.drinkwater.api.v1.controller.UserControllerV1;
import br.com.drinkwater.config.TestMessageSourceConfig;
import br.com.drinkwater.usermanagement.dto.AlarmSettingsDTO;
import br.com.drinkwater.usermanagement.dto.PersonalDTO;
import br.com.drinkwater.usermanagement.dto.PhysicalDTO;
import br.com.drinkwater.usermanagement.dto.UserDTO;
import br.com.drinkwater.usermanagement.model.BiologicalSex;
import br.com.drinkwater.usermanagement.model.HeightUnit;
import br.com.drinkwater.usermanagement.model.WeightUnit;
import br.com.drinkwater.usermanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
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

@WebMvcTest(controllers = UserControllerV1.class)
@ActiveProfiles("test")
@Import(TestMessageSourceConfig.class)
final class AlarmTimeValidatorTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Autowired private MessageSource messageSource;

    @MockitoBean private UserService userService;

    private UserDTO createUserDTO(LocalTime dailyStartTime, LocalTime dailyEndTime) {
        AlarmSettingsDTO alarmSettings =
                new AlarmSettingsDTO(2000, 30, dailyStartTime, dailyEndTime);

        PersonalDTO personal =
                new PersonalDTO("John", "Doe", LocalDate.of(1990, 1, 1), BiologicalSex.MALE);

        PhysicalDTO physical =
                new PhysicalDTO(
                        new BigDecimal("80"), WeightUnit.KG, new BigDecimal("180"), HeightUnit.CM);

        return new UserDTO("test@example.com", personal, physical, alarmSettings);
    }

    @Test
    void givenInvalidEarlyStartTime_whenCreateUser_thenReturnBadRequest() throws Exception {
        // Given
        // dailyStartTime antes das 06:00 (horário de abertura)
        var invalidUserDTO = this.createUserDTO(LocalTime.of(5, 0), LocalTime.of(9, 0));

        var expectedFieldMessage =
                this.messageSource.getMessage(
                        "alarm-settings.start.business-hours",
                        new Object[] {LocalTime.of(6, 0), LocalTime.of(22, 0)},
                        Locale.of("en", "US"));
        var expectedDetail =
                this.messageSource.getMessage("validation.error", null, Locale.of("en", "US"));

        // When & Then
        this.mockMvc
                .perform(
                        post("/api/v1/users")
                                .with(
                                        jwt().jwt(
                                                        builder ->
                                                                builder.claim(
                                                                        "sub",
                                                                        USER_UUID.toString()))
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "SCOPE_drinkwater:v1:user:profile:create")))
                                .content(objectMapper.writeValueAsString(invalidUserDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.type").value("https://www.drinkwater.com.br/validation-error"))
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.detail").value(expectedDetail))
                .andExpect(jsonPath("$.instance").value("/api/v1/users"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.field=='settings.dailyStartTime')]").exists())
                .andExpect(
                        jsonPath("$.errors[?(@.field=='settings.dailyStartTime')].message")
                                .value(expectedFieldMessage));

        verifyNoInteractions(userService);
    }

    @Test
    void givenInvalidLateEndTime_whenCreateUser_thenReturnBadRequest() throws Exception {
        // Given
        // dailyEndTime após as 22:00 (horário de fechamento)
        UserDTO invalidUserDTO = createUserDTO(LocalTime.of(7, 0), LocalTime.of(23, 0));

        String expectedMessage =
                messageSource.getMessage(
                        "alarm-settings.end.business-hours",
                        new Object[] {LocalTime.of(6, 0), LocalTime.of(22, 0)},
                        Locale.of("en", "US"));

        // When & Then
        mockMvc.perform(
                        post("/api/v1/users")
                                .with(
                                        jwt().jwt(
                                                        builder ->
                                                                builder.claim(
                                                                        "sub",
                                                                        USER_UUID.toString()))
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "SCOPE_drinkwater:v1:user:profile:create")))
                                .content(objectMapper.writeValueAsString(invalidUserDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field=='settings.dailyEndTime')]").exists())
                .andExpect(
                        jsonPath("$.errors[?(@.field=='settings.dailyEndTime')].message")
                                .value(expectedMessage));

        verifyNoInteractions(userService);
    }

    @Test
    void givenInvalidStartAfterEnd_whenCreateUser_thenReturnBadRequest() throws Exception {
        // Given
        // dailyStartTime não é anterior à dailyEndTime (ex.: 10:00 >= 09:00)
        UserDTO invalidUserDTO = createUserDTO(LocalTime.of(10, 0), LocalTime.of(9, 0));

        String expectedMessage =
                messageSource.getMessage(
                        "alarm-settings.start-before-end", null, Locale.of("en", "US"));

        // When & Then
        mockMvc.perform(
                        post("/api/v1/users")
                                .with(
                                        jwt().jwt(
                                                        builder ->
                                                                builder.claim(
                                                                        "sub",
                                                                        USER_UUID.toString()))
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "SCOPE_drinkwater:v1:user:profile:create")))
                                .content(objectMapper.writeValueAsString(invalidUserDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field=='settings.dailyStartTime')]").exists())
                .andExpect(
                        jsonPath("$.errors[?(@.field=='settings.dailyStartTime')].message")
                                .value(expectedMessage));

        verifyNoInteractions(userService);
    }

    @Test
    void givenInvalidBothTimes_whenUpdateUser_thenReturnBadRequest() throws Exception {
        // Given
        // Na atualização, ambos os horários estão fora do intervalo permitido:
        // dailyStartTime antes das 06:00 e dailyEndTime após as 22:00.
        UserDTO invalidUserDTO = createUserDTO(LocalTime.of(5, 0), LocalTime.of(23, 0));

        String expectedStartMessage =
                messageSource.getMessage(
                        "alarm-settings.start.business-hours",
                        new Object[] {LocalTime.of(6, 0), LocalTime.of(22, 0)},
                        Locale.of("en", "US"));
        String expectedEndMessage =
                messageSource.getMessage(
                        "alarm-settings.end.business-hours",
                        new Object[] {LocalTime.of(6, 0), LocalTime.of(22, 0)},
                        Locale.of("en", "US"));

        // When & Then
        mockMvc.perform(
                        put("/api/v1/users")
                                .with(
                                        jwt().jwt(
                                                        builder ->
                                                                builder.claim(
                                                                        "sub",
                                                                        USER_UUID.toString()))
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "SCOPE_drinkwater:v1:user:profile:update")))
                                .content(objectMapper.writeValueAsString(invalidUserDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field=='settings.dailyStartTime')]").exists())
                .andExpect(jsonPath("$.errors[?(@.field=='settings.dailyEndTime')]").exists())
                .andExpect(
                        jsonPath("$.errors[?(@.field=='settings.dailyStartTime')].message")
                                .value(expectedStartMessage))
                .andExpect(
                        jsonPath("$.errors[?(@.field=='settings.dailyEndTime')].message")
                                .value(expectedEndMessage));

        verifyNoInteractions(userService);
    }

    @Test
    void givenEndTimeBeforeBusinessStart_whenCreateUser_thenReturnBadRequest() throws Exception {
        // Given - end time before 06:00 to cover end.isBefore(BUSINESS_START) branch
        UserDTO invalidUserDTO = createUserDTO(LocalTime.of(7, 0), LocalTime.of(5, 0));

        String expectedEndMessage =
                messageSource.getMessage(
                        "alarm-settings.end.business-hours",
                        new Object[] {LocalTime.of(6, 0), LocalTime.of(22, 0)},
                        Locale.of("en", "US"));

        // When & Then
        mockMvc.perform(
                        post("/api/v1/users")
                                .with(
                                        jwt().jwt(
                                                        builder ->
                                                                builder.claim(
                                                                        "sub",
                                                                        USER_UUID.toString()))
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "SCOPE_drinkwater:v1:user:profile:create")))
                                .content(objectMapper.writeValueAsString(invalidUserDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field=='settings.dailyEndTime')]").exists())
                .andExpect(
                        jsonPath("$.errors[?(@.field=='settings.dailyEndTime')].message")
                                .value(expectedEndMessage));

        verifyNoInteractions(userService);
    }

    @Test
    void givenStartTimeBeforeBusinessStart_whenCreateUser_thenReturnBadRequest() throws Exception {
        // Given
        // Teste específico para a condição: start.isBefore(BUSINESS_START)
        String jsonWithStartBeforeBusinessStart =
                """
            {
                "email":"test@example.com",
                "personal":{
                    "firstName":"John",
                    "lastName":"Doe",
                    "birthDate":"1990-01-01",
                    "biologicalSex":"MALE"
                },
                "physical":{
                    "weight":80,
                    "weightUnit":"KG",
                    "height":180,
                    "heightUnit":"CM"
                },
                "settings":{
                    "goal":2000,
                    "intervalMinutes":30,
                    "dailyStartTime":"05:30:00",
                    "dailyEndTime":"18:00:00"
                }
            }
            """;

        String expectedMessage =
                messageSource.getMessage(
                        "alarm-settings.start.business-hours",
                        new Object[] {LocalTime.of(6, 0), LocalTime.of(22, 0)},
                        Locale.of("en", "US"));

        String expectedDetail =
                messageSource.getMessage("validation.error", null, Locale.of("en", "US"));

        // When & Then
        mockMvc.perform(
                        post("/api/v1/users")
                                .with(
                                        jwt().jwt(
                                                        builder ->
                                                                builder.claim(
                                                                        "sub",
                                                                        USER_UUID.toString()))
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "SCOPE_drinkwater:v1:user:profile:create")))
                                .content(jsonWithStartBeforeBusinessStart)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.type").value("https://www.drinkwater.com.br/validation-error"))
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.detail").value(expectedDetail))
                .andExpect(jsonPath("$.instance").value("/api/v1/users"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.field=='settings.dailyStartTime')]").exists())
                .andExpect(
                        jsonPath(
                                        "$.errors[?(@.field=='settings.dailyStartTime' && @.message=='%s')]"
                                                .formatted(expectedMessage))
                                .exists());

        verifyNoInteractions(userService);
    }

    @Test
    void givenStartTimeAfterBusinessEnd_whenCreateUser_thenReturnBadRequest() throws Exception {
        // Given
        // Teste específico para a condição: start.isAfter(BUSINESS_END)
        String jsonWithStartAfterBusinessEnd =
                """
            {
                "email":"test@example.com",
                "personal":{
                    "firstName":"John",
                    "lastName":"Doe",
                    "birthDate":"1990-01-01",
                    "biologicalSex":"MALE"
                },
                "physical":{
                    "weight":80,
                    "weightUnit":"KG",
                    "height":180,
                    "heightUnit":"CM"
                },
                "settings":{
                    "goal":2000,
                    "intervalMinutes":30,
                    "dailyStartTime":"22:30:00",
                    "dailyEndTime":"22:45:00"
                }
            }
            """;

        String expectedMessage =
                messageSource.getMessage(
                        "alarm-settings.start.business-hours",
                        new Object[] {LocalTime.of(6, 0), LocalTime.of(22, 0)},
                        Locale.of("en", "US"));

        String expectedDetail =
                messageSource.getMessage("validation.error", null, Locale.of("en", "US"));

        // When & Then
        mockMvc.perform(
                        post("/api/v1/users")
                                .with(
                                        jwt().jwt(
                                                        builder ->
                                                                builder.claim(
                                                                        "sub",
                                                                        USER_UUID.toString()))
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "SCOPE_drinkwater:v1:user:profile:create")))
                                .content(jsonWithStartAfterBusinessEnd)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.type").value("https://www.drinkwater.com.br/validation-error"))
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.detail").value(expectedDetail))
                .andExpect(jsonPath("$.instance").value("/api/v1/users"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.field=='settings.dailyStartTime')]").exists())
                .andExpect(
                        jsonPath(
                                        "$.errors[?(@.field=='settings.dailyStartTime' && @.message=='%s')]"
                                                .formatted(expectedMessage))
                                .exists());

        verifyNoInteractions(userService);
    }
}
