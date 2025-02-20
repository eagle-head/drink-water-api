package br.com.drinkwater.usermanagement.controller;

import br.com.drinkwater.usermanagement.config.TestMessageSourceConfig;
import br.com.drinkwater.usermanagement.dto.AlarmSettingsDTO;
import br.com.drinkwater.usermanagement.dto.UserDTO;
import br.com.drinkwater.usermanagement.exception.UserAlreadyExistsException;
import br.com.drinkwater.usermanagement.exception.UserNotFoundException;
import br.com.drinkwater.usermanagement.service.UserService;
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
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Locale;

import static br.com.drinkwater.usermanagement.constants.UserTestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@ActiveProfiles("test")
@Import(TestMessageSourceConfig.class)
public final class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

    @MockitoBean
    private UserService userService;

    @Test
    public void givenValidJwtToken_whenGetCurrentUser_thenReturnUserResponse() throws Exception {

        when(this.userService.getUserByPublicId(USER_UUID)).thenReturn(USER_RESPONSE_DTO);

        mockMvc
                .perform(get("/users/me")
                        .with(jwt()
                                .jwt(builder -> builder.claim("sub", USER_UUID.toString())
                                        .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId")
                        .value(USER_UUID.toString()))
                .andExpect(jsonPath("$.email")
                        .value(USER_RESPONSE_DTO.email()))
                .andExpect(jsonPath("$.personal.firstName")
                        .value(USER_RESPONSE_DTO.personal().firstName()))
                .andExpect(jsonPath("$.personal.lastName")
                        .value(USER_RESPONSE_DTO.personal().lastName()))
                .andExpect(jsonPath("$.physical.weight")
                        .value(USER_RESPONSE_DTO.physical().weight()))
                .andExpect(jsonPath("$.physical.height")
                        .value(USER_RESPONSE_DTO.physical().height()))
                .andExpect(jsonPath("$.settings.goal")
                        .value(USER_RESPONSE_DTO.settings().goal()))
                .andExpect(jsonPath("$.settings.intervalMinutes")
                        .value(USER_RESPONSE_DTO.settings().intervalMinutes()));
    }

    @Test
    public void givenValidJwtTokenAndValidUserDTO_whenCreateUser_thenReturnCreatedUserResponse() throws Exception {

        when(this.userService.createUser(USER_UUID, USER_DTO)).thenReturn(USER_RESPONSE_DTO);

        mockMvc
                .perform(post("/users")
                        .with(jwt()
                                .jwt(builder -> builder.claim("sub", USER_UUID.toString())
                                        .build()))
                        .content(this.objectMapper.writeValueAsString(USER_DTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.publicId")
                        .value(USER_UUID.toString()))
                .andExpect(jsonPath("$.email")
                        .value(USER_RESPONSE_DTO.email()))
                .andExpect(jsonPath("$.personal.firstName")
                        .value(USER_RESPONSE_DTO.personal().firstName()))
                .andExpect(jsonPath("$.personal.lastName")
                        .value(USER_RESPONSE_DTO.personal().lastName()))
                .andExpect(jsonPath("$.physical.weight")
                        .value(USER_RESPONSE_DTO.physical().weight()))
                .andExpect(jsonPath("$.physical.height")
                        .value(USER_RESPONSE_DTO.physical().height()))
                .andExpect(jsonPath("$.settings.goal")
                        .value(USER_RESPONSE_DTO.settings().goal()))
                .andExpect(jsonPath("$.settings.intervalMinutes")
                        .value(USER_RESPONSE_DTO.settings().intervalMinutes()));
    }

    @Test
    public void givenValidJwtTokenAndValidUserDTO_whenUpdateCurrentUser_thenReturnUpdatedUserResponse() throws Exception {

        when(this.userService.updateUser(USER_UUID, UPDATE_USER_DTO)).thenReturn(USER_RESPONSE_DTO);

        mockMvc
                .perform(put("/users")
                        .with(jwt()
                                .jwt(builder -> builder.claim("sub", USER_UUID.toString())
                                        .build()))
                        .content(this.objectMapper.writeValueAsString(UPDATE_USER_DTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId")
                        .value(USER_UUID.toString()))
                .andExpect(jsonPath("$.email")
                        .value(USER_RESPONSE_DTO.email()))
                .andExpect(jsonPath("$.personal.firstName")
                        .value(USER_RESPONSE_DTO.personal().firstName()))
                .andExpect(jsonPath("$.personal.lastName")
                        .value(USER_RESPONSE_DTO.personal().lastName()))
                .andExpect(jsonPath("$.physical.weight")
                        .value(USER_RESPONSE_DTO.physical().weight()))
                .andExpect(jsonPath("$.physical.height")
                        .value(USER_RESPONSE_DTO.physical().height()))
                .andExpect(jsonPath("$.settings.goal")
                        .value(USER_RESPONSE_DTO.settings().goal()))
                .andExpect(jsonPath("$.settings.intervalMinutes")
                        .value(USER_RESPONSE_DTO.settings().intervalMinutes()));
    }

    @Test
    public void givenValidJwtToken_whenDeleteCurrentUser_thenReturnNoContent() throws Exception {

        doNothing().when(this.userService).deleteByPublicId(USER_UUID);

        mockMvc
                .perform(delete("/users")
                        .with(jwt().jwt(builder -> builder.claim("sub", USER_UUID.toString()).build())))
                .andExpect(status().isNoContent());

        verify(this.userService, times(1)).deleteByPublicId(USER_UUID);
    }

    @Test
    public void givenExistingUser_whenCreateUser_thenThrowUserAlreadyExistsException() throws Exception {
        when(this.userService.createUser(USER_UUID, USER_DTO))
                .thenThrow(UserAlreadyExistsException.class);

        mockMvc.perform(post("/users")
                        .with(jwt()
                                .jwt(builder -> builder.claim("sub", USER_UUID.toString())
                                        .build()))
                        .content(this.objectMapper.writeValueAsString(USER_DTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type")
                        .value("https://www.drinkwater.com.br/user-already-exists"))
                .andExpect(jsonPath("$.title")
                        .value(HttpStatus.CONFLICT.getReasonPhrase()))
                .andExpect(jsonPath("$.detail")
                        .value(this.messageSource.getMessage(
                                "user.already.exists.detail",
                                null,
                                new Locale("en", "US"))
                        )
                )
                .andExpect(jsonPath("$.status")
                        .value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.instance")
                        .value("/users"));
    }

    @Test
    public void givenValidJwtToken_whenGetCurrentUser_thenThrowUserNotFoundException() throws Exception {

        when(this.userService.getUserByPublicId(USER_UUID)).thenThrow(UserNotFoundException.class);

        mockMvc
                .perform(get("/users/me")
                        .with(jwt().jwt(builder -> builder.claim("sub", USER_UUID.toString()).build()))
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type")
                        .value("https://www.drinkwater.com.br/user-not-found"))
                .andExpect(jsonPath("$.title")
                        .value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.detail")
                        .value(this.messageSource.getMessage(
                                "user.not.found.detail",
                                null,
                                new Locale("en", "US"))
                        )
                )
                .andExpect(jsonPath("$.status")
                        .value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.instance")
                        .value("/users/me"));
    }
}