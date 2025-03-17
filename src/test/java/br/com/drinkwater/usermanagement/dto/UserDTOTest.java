package br.com.drinkwater.usermanagement.dto;

import br.com.drinkwater.config.TestMessageSourceConfig;
import br.com.drinkwater.usermanagement.controller.UserController;
import br.com.drinkwater.usermanagement.model.BiologicalSex;
import br.com.drinkwater.usermanagement.model.HeightUnit;
import br.com.drinkwater.usermanagement.model.WeightUnit;
import br.com.drinkwater.usermanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@Import(TestMessageSourceConfig.class)
public class UserDTOTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

    @MockitoBean
    private UserService userService;

    private static final PersonalDTO VALID_PERSONAL = new PersonalDTO(
            "John",
            "Doe",
            OffsetDateTime.now(ZoneOffset.UTC).toLocalDate().minusYears(30),
            BiologicalSex.MALE
    );

    private static final PhysicalDTO VALID_PHYSICAL = new PhysicalDTO(
            BigDecimal.valueOf(70),
            WeightUnit.KG,
            BigDecimal.valueOf(175),
            HeightUnit.CM
    );

    private static final AlarmSettingsDTO VALID_SETTINGS = new AlarmSettingsDTO(
            2000,
            30,
            LocalTime.of(9, 0, 0),
            LocalTime.of(17, 0, 0)
    );

    private static final String DUMMY_SUBJECT = UUID.randomUUID().toString();

    @Test
    public void givenUserDTOWithNullEmail_whenCreateUser_thenBadRequest() throws Exception {
        PersonalDTO personalWithMillis = new PersonalDTO(
                "John",
                "Doe",
                OffsetDateTime.now(ZoneOffset.UTC).toLocalDate().minusYears(30),
                BiologicalSex.MALE
        );

        UserDTO invalidDTO = new UserDTO(null, personalWithMillis, VALID_PHYSICAL, VALID_SETTINGS);

        String expectedEmailNotBlank = this.messageSource.getMessage(
                "userDTO.email.notBlank",
                null,
                new Locale("en", "US")
        );
        String expectedDetail = this.messageSource.getMessage(
                "validation.error.detail",
                null,
                new Locale("en", "US")
        );

        mockMvc.perform(post("/users")
                        .with(jwt()
                                .jwt(builder -> builder.claim("sub", DUMMY_SUBJECT).build()))
                        .content(objectMapper.writeValueAsString(invalidDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("https://www.drinkwater.com.br/validation-error"))
                .andExpect(jsonPath("$.title").value(BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.detail").value(expectedDetail))
                .andExpect(jsonPath("$.instance").value("/users"))
                .andExpect(jsonPath(
                        "$.errors[?(@.field=='email')].message",
                        containsInAnyOrder(expectedEmailNotBlank))
                );
    }

    @Test
    public void givenUserDTOWithBlankEmail_whenCreateUser_thenBadRequest() throws Exception {
        UserDTO invalidDTO = new UserDTO("   ", VALID_PERSONAL, VALID_PHYSICAL, VALID_SETTINGS);

        String expectedEmailNotBlank = this.messageSource.getMessage(
                "userDTO.email.notBlank",
                null,
                new Locale("en", "US")
        );
        String expectedEmailEmail = this.messageSource.getMessage(
                "userDTO.email.email",
                null,
                new Locale("en", "US")
        );
        String expectedDetail = this.messageSource.getMessage(
                "validation.error.detail",
                null,
                new Locale("en", "US")
        );

        mockMvc.perform(post("/users")
                        .with(jwt()
                                .jwt(builder -> builder.claim("sub", DUMMY_SUBJECT).build()))
                        .content(objectMapper.writeValueAsString(invalidDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type")
                        .value("https://www.drinkwater.com.br/validation-error"))
                .andExpect(jsonPath("$.title").value(BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.detail").value(expectedDetail))
                .andExpect(jsonPath("$.instance").value("/users"))
                .andExpect(jsonPath(
                        "$.errors[?(@.field=='email')].message",
                        containsInAnyOrder(expectedEmailNotBlank, expectedEmailEmail, expectedEmailEmail))
                );
    }

    @Test
    public void givenUserDTOWithInvalidEmailFormat_whenCreateUser_thenBadRequest() throws Exception {
        UserDTO invalidDTO = new UserDTO("invalid-email", VALID_PERSONAL, VALID_PHYSICAL, VALID_SETTINGS);

        String expectedEmailMessage = this.messageSource.getMessage(
                "userDTO.email.email",
                null,
                new Locale("en", "US")
        );
        String expectedDetail = this.messageSource.getMessage(
                "validation.error.detail",
                null,
                new Locale("en", "US")
        );

        mockMvc.perform(post("/users")
                        .with(jwt()
                                .jwt(builder -> builder.claim("sub", DUMMY_SUBJECT).build()))
                        .content(objectMapper.writeValueAsString(invalidDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type")
                        .value("https://www.drinkwater.com.br/validation-error"))
                .andExpect(jsonPath("$.title").value(BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.detail").value(expectedDetail))
                .andExpect(jsonPath("$.instance").value("/users"))
                .andExpect(jsonPath(
                        "$.errors[?(@.field=='email')].message",
                        containsInAnyOrder(expectedEmailMessage, expectedEmailMessage))
                );
    }

    @Test
    public void givenUserDTOWithEmailNotMatchingPattern_whenCreateUser_thenBadRequest() throws Exception {
        UserDTO invalidDTO = new UserDTO("user@domain.123", VALID_PERSONAL, VALID_PHYSICAL, VALID_SETTINGS);

        String expectedEmailMessage = this.messageSource.getMessage(
                "userDTO.email.pattern",
                null,
                new Locale("en", "US")
        );
        String expectedDetail = this.messageSource.getMessage(
                "validation.error.detail",
                null,
                new Locale("en", "US")
        );

        mockMvc.perform(post("/users")
                        .with(jwt()
                                .jwt(builder -> builder.claim("sub", DUMMY_SUBJECT).build()))
                        .content(objectMapper.writeValueAsString(invalidDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type")
                        .value("https://www.drinkwater.com.br/validation-error"))
                .andExpect(jsonPath("$.title").value(BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.detail").value(expectedDetail))
                .andExpect(jsonPath("$.instance").value("/users"))
                .andExpect(jsonPath(
                        "$.errors[?(@.field=='email')].message",
                        containsInAnyOrder(expectedEmailMessage))
                );
    }

    @Test
    public void givenUserDTOWithEmailExceedingMaxSize_whenCreateUser_thenBadRequest() throws Exception {
        String longLocalPart = "a".repeat(251);
        String longEmail = longLocalPart + "@e.co";

        PersonalDTO validPersonal = new PersonalDTO(
                "John",
                "Doe",
                OffsetDateTime.now(ZoneOffset.UTC).toLocalDate().minusYears(30),
                BiologicalSex.MALE
        );

        UserDTO invalidDTO = new UserDTO(longEmail, validPersonal, VALID_PHYSICAL, VALID_SETTINGS);

        String rawEmailSizeMessage = this.messageSource.getMessage("userDTO.email.size", null, new Locale("en", "US"));
        String expectedEmailSizeMessage = rawEmailSizeMessage.replace("{max}", "255");
        String expectedEmailMessage = this.messageSource.getMessage("userDTO.email.email", null, new Locale("en", "US"));
        String expectedDetail = this.messageSource.getMessage("validation.error.detail", null, new Locale("en", "US"));

        mockMvc.perform(post("/users")
                        .with(jwt()
                                .jwt(builder -> builder.claim("sub", DUMMY_SUBJECT).build()))
                        .content(objectMapper.writeValueAsString(invalidDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type")
                        .value("https://www.drinkwater.com.br/validation-error"))
                .andExpect(jsonPath("$.title").value(BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.detail").value(expectedDetail))
                .andExpect(jsonPath("$.instance").value("/users"))
                .andExpect(jsonPath("$.errors[?(@.field=='email')].message",
                        containsInAnyOrder(expectedEmailMessage, expectedEmailSizeMessage)));
    }

    @Test
    public void givenUserDTOWithNullPersonal_whenCreateUser_thenBadRequest() throws Exception {
        UserDTO invalidDTO = new UserDTO("valid@example.com", null, VALID_PHYSICAL, VALID_SETTINGS);

        String expectedPersonalMessage = this.messageSource.getMessage(
                "userDTO.personal.notNull",
                null,
                new Locale("en", "US")
        );
        String expectedDetail = this.messageSource.getMessage(
                "validation.error.detail",
                null,
                new Locale("en", "US")
        );

        mockMvc.perform(post("/users")
                        .with(jwt().jwt(builder -> builder.claim("sub", DUMMY_SUBJECT).build()))
                        .content(objectMapper.writeValueAsString(invalidDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type")
                        .value("https://www.drinkwater.com.br/validation-error"))
                .andExpect(jsonPath("$.title").value(BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.detail").value(expectedDetail))
                .andExpect(jsonPath("$.instance").value("/users"))
                .andExpect(jsonPath("$.errors[?(@.field=='personal')].message")
                        .value(expectedPersonalMessage)
                );
    }

    @Test
    public void givenUserDTOWithNullPhysical_whenCreateUser_thenBadRequest() throws Exception {
        PersonalDTO validPersonal = new PersonalDTO(
                "John",
                "Doe",
                OffsetDateTime.now(ZoneOffset.UTC).toLocalDate().minusYears(30),
                BiologicalSex.MALE
        );

        UserDTO invalidDTO = new UserDTO("valid@example.com", validPersonal, null, VALID_SETTINGS);

        String expectedPhysicalMessage = this.messageSource.getMessage(
                "userDTO.physical.notNull",
                null,
                new Locale("en", "US")
        );
        String expectedDetail = this.messageSource.getMessage(
                "validation.error.detail",
                null,
                new Locale("en", "US")
        );

        this.mockMvc
                .perform(post("/users")
                        .with(jwt().jwt(builder -> builder.claim("sub", DUMMY_SUBJECT).build()))
                        .content(objectMapper.writeValueAsString(invalidDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type")
                        .value("https://www.drinkwater.com.br/validation-error"))
                .andExpect(jsonPath("$.title")
                        .value(BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status")
                        .value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.detail")
                        .value(expectedDetail))
                .andExpect(jsonPath("$.instance")
                        .value("/users"))
                .andExpect(jsonPath("$.errors[?(@.field=='physical')].message")
                        .value(expectedPhysicalMessage)
                );
    }

    @Test
    public void givenUserDTOWithNullSettings_whenCreateUser_thenBadRequest() throws Exception {
        var validPersonal = new PersonalDTO(
                "John",
                "Doe",
                OffsetDateTime.now(ZoneOffset.UTC).toLocalDate().minusYears(30),
                BiologicalSex.MALE
        );

        var invalidDTO = new UserDTO("valid@example.com", validPersonal, VALID_PHYSICAL, null);

        var expectedSettingsMessage = this.messageSource.getMessage(
                "userDTO.settings.notNull",
                null,
                new Locale("en", "US")
        );
        var expectedDetail = this.messageSource.getMessage("validation.error.detail",
                null,
                new Locale("en", "US")
        );

        this.mockMvc
                .perform(post("/users")
                        .with(jwt()
                                .jwt(builder -> builder.claim("sub", DUMMY_SUBJECT).build()))
                        .content(objectMapper.writeValueAsString(invalidDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("https://www.drinkwater.com.br/validation-error"))
                .andExpect(jsonPath("$.title").value(BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.detail").value(expectedDetail))
                .andExpect(jsonPath("$.instance").value("/users"))
                .andExpect(jsonPath("$.errors[?(@.field=='settings')].message").value(expectedSettingsMessage));
    }
}
