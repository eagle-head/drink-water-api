package br.com.drinkwater.usermanagement.validation;

import br.com.drinkwater.usermanagement.controller.UserController;
import br.com.drinkwater.usermanagement.dto.AlarmSettingsDTO;
import br.com.drinkwater.usermanagement.dto.AlarmSettingsResponseDTO;
import br.com.drinkwater.usermanagement.dto.PersonalDTO;
import br.com.drinkwater.usermanagement.dto.PhysicalDTO;
import br.com.drinkwater.usermanagement.dto.UserDTO;
import br.com.drinkwater.usermanagement.dto.UserResponseDTO;
import br.com.drinkwater.usermanagement.model.BiologicalSex;
import br.com.drinkwater.usermanagement.model.WeightUnit;
import br.com.drinkwater.usermanagement.model.HeightUnit;
import br.com.drinkwater.usermanagement.service.UserService;
import br.com.drinkwater.config.TestMessageSourceConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;

import static br.com.drinkwater.usermanagement.constants.UserTestConstants.USER_UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@ActiveProfiles("test")
@Import(TestMessageSourceConfig.class)
public final class BirthDateValidatorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

    @MockitoBean
    private UserService userService;

    private static final UserResponseDTO DUMMY_USER_RESPONSE = new UserResponseDTO(
            USER_UUID,
            "test@example.com",
            new PersonalDTO("John", "Doe", LocalDate.now().minusYears(13), BiologicalSex.MALE),
            new PhysicalDTO(new BigDecimal("70.0"), WeightUnit.KG, new BigDecimal("175.0"), HeightUnit.CM),
            new AlarmSettingsResponseDTO(2000, 30, LocalTime.of(7, 0), LocalTime.of(22, 0))
    );

    private UserDTO buildUserDTOWithBirthDate(LocalDate birthDate) {
        PersonalDTO personal = new PersonalDTO("John", "Doe", birthDate, BiologicalSex.MALE);
        PhysicalDTO physical = new PhysicalDTO(new BigDecimal("70.0"), WeightUnit.KG, new BigDecimal("175.0"), HeightUnit.CM);
        AlarmSettingsDTO settings = new AlarmSettingsDTO(2000, 30, LocalTime.of(7, 0), LocalTime.of(22, 0));
        return new UserDTO("test@example.com", personal, physical, settings);
    }

    @Test
    public void givenFutureBirthDate_whenCreateUser_thenReturnBadRequest() throws Exception {
        var invalidBirthDate = LocalDate.now().plusDays(1);
        var userDTO = this.buildUserDTOWithBirthDate(invalidBirthDate);
        var jsonPayload = this.objectMapper.writeValueAsString(userDTO);
        var locale = new Locale("en", "US");
        var expectedDetail = this.messageSource.getMessage("validation.error.detail", null, locale);
        var expectedMessage = this.messageSource.getMessage("validation.birthdate.invalid", null, locale);

        mockMvc
                .perform(post("/users")
                        .with(jwt().jwt(builder -> builder.claim("sub", USER_UUID.toString()).build()))
                        .content(jsonPayload)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.type")
                        .value("https://www.drinkwater.com.br/validation-error"))
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.detail").value(expectedDetail))
                .andExpect(jsonPath("$.instance").value("/users"))
                .andExpect(jsonPath("$.errors[?(@.field=='personal.birthDate')].message")
                        .value(expectedMessage));
    }

    @Test
    public void givenNullBirthDate_whenCreateUser_thenReturnBadRequest() throws Exception {
        var personal = new PersonalDTO("John", "Doe", null, BiologicalSex.MALE);
        var physical = new PhysicalDTO(new BigDecimal("70.0"), WeightUnit.KG, new BigDecimal("175.0"), HeightUnit.CM);
        var settings = new AlarmSettingsDTO(2000, 30, LocalTime.of(7, 0), LocalTime.of(22, 0));
        var userDTO = new UserDTO("test@example.com", personal, physical, settings);
        var jsonPayload = this.objectMapper.writeValueAsString(userDTO);

        var locale = new Locale("en", "US");

        var expectedDetail = this.messageSource.getMessage("validation.error.detail", null, locale);
        var expectedMessage = this.messageSource.getMessage("personalDTO.birthDate.notNull", null, locale);

        mockMvc.perform(post("/users")
                        .with(jwt().jwt(builder -> builder.claim("sub", USER_UUID.toString()).build()))
                        .content(jsonPayload)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.type")
                        .value("https://www.drinkwater.com.br/validation-error"))
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.detail").value(expectedDetail))
                .andExpect(jsonPath("$.instance").value("/users"))
                .andExpect(jsonPath("$.errors[?(@.field=='personal.birthDate')].message")
                        .value(expectedMessage));
    }

    @Test
    public void givenBirthDateExactlyMinimumAge_whenCreateUser_thenReturnCreatedUserResponse() throws Exception {
        var minAgeBirthDate = LocalDate.now().minusYears(13);
        var userDTO = this.buildUserDTOWithBirthDate(minAgeBirthDate);
        var jsonPayload = this.objectMapper.writeValueAsString(userDTO);

        when(this.userService.createUser(eq(USER_UUID), any(UserDTO.class))).thenReturn(DUMMY_USER_RESPONSE);

        mockMvc
                .perform(post("/users")
                        .with(jwt().jwt(builder -> builder.claim("sub", USER_UUID.toString()).build()))
                        .content(jsonPayload)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.publicId").value(USER_UUID.toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.personal.firstName").value("John"))
                .andExpect(jsonPath("$.personal.lastName").value("Doe"))
                .andExpect(jsonPath("$.personal.birthDate")
                        .value(DUMMY_USER_RESPONSE.personal().birthDate().toString()))
                .andExpect(jsonPath("$.personal.biologicalSex").value(BiologicalSex.MALE.toString()));
    }

    @Test
    public void givenBirthDateExactlyMaximumAge_whenCreateUser_thenReturnCreatedUserResponse() throws Exception {
        var maxAgeBirthDate = LocalDate.now().minusYears(99);
        var userDTO = this.buildUserDTOWithBirthDate(maxAgeBirthDate);
        var jsonPayload = this.objectMapper.writeValueAsString(userDTO);

        when(this.userService.createUser(eq(USER_UUID), any(UserDTO.class))).thenReturn(DUMMY_USER_RESPONSE);

        mockMvc
                .perform(post("/users")
                        .with(jwt().jwt(builder -> builder.claim("sub", USER_UUID.toString()).build()))
                        .content(jsonPayload)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.publicId").value(USER_UUID.toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.personal.firstName").value("John"))
                .andExpect(jsonPath("$.personal.lastName").value("Doe"))
                .andExpect(jsonPath("$.personal.birthDate").value(DUMMY_USER_RESPONSE.personal().birthDate().toString()))
                .andExpect(jsonPath("$.personal.biologicalSex").value(BiologicalSex.MALE.toString()));
    }
}
