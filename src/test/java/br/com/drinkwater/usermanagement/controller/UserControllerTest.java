package br.com.drinkwater.usermanagement.controller;

import static br.com.drinkwater.usermanagement.constants.UserTestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.drinkwater.api.v1.controller.UserControllerV1;
import br.com.drinkwater.config.TestMessageSourceConfig;
import br.com.drinkwater.usermanagement.exception.UserAlreadyExistsException;
import br.com.drinkwater.usermanagement.exception.UserNotFoundException;
import br.com.drinkwater.usermanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(controllers = UserControllerV1.class)
@ActiveProfiles("test")
@Import(TestMessageSourceConfig.class)
final class UserControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Autowired private MessageSource messageSource;

    @MockitoBean private UserService userService;

    @Test
    void givenValidJwtToken_whenGetCurrentUser_thenReturnUserResponse() throws Exception {
        // Given
        when(userService.getUserByPublicId(USER_UUID)).thenReturn(USER_RESPONSE_DTO);

        // When & Then
        var result =
                mockMvc.perform(
                                get("/api/v1/users/me")
                                        .with(
                                                jwt().jwt(
                                                                builder ->
                                                                        builder.claim(
                                                                                "sub",
                                                                                USER_UUID
                                                                                        .toString()))
                                                        .authorities(
                                                                new SimpleGrantedAuthority(
                                                                        "SCOPE_drinkwater:v1:user:profile:read")))
                                        .accept(MediaType.APPLICATION_JSON)
                                        .header("Accept-Language", "en-US"))
                        .andExpect(status().isOk());

        expectUserResponse(result);
    }

    @Test
    void givenValidJwtTokenAndValidUserDTO_whenCreateUser_thenReturnCreatedUserResponse()
            throws Exception {
        // Given
        when(userService.createUser(USER_UUID, USER_DTO)).thenReturn(USER_RESPONSE_DTO);

        // When & Then
        var result =
                mockMvc.perform(
                                post("/api/v1/users")
                                        .with(
                                                jwt().jwt(
                                                                builder ->
                                                                        builder.claim(
                                                                                "sub",
                                                                                USER_UUID
                                                                                        .toString()))
                                                        .authorities(
                                                                new SimpleGrantedAuthority(
                                                                        "SCOPE_drinkwater:v1:user:profile:create")))
                                        .content(this.objectMapper.writeValueAsString(USER_DTO))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .accept(MediaType.APPLICATION_JSON)
                                        .header("Accept-Language", "en-US"))
                        .andExpect(status().isCreated());

        expectUserResponse(result);
    }

    @Test
    void givenValidJwtTokenAndValidUserDTO_whenUpdateCurrentUser_thenReturnUpdatedUserResponse()
            throws Exception {
        // Given
        when(userService.updateUser(USER_UUID, UPDATE_USER_DTO)).thenReturn(USER_RESPONSE_DTO);

        // When & Then
        var result =
                mockMvc.perform(
                                put("/api/v1/users")
                                        .with(
                                                jwt().jwt(
                                                                builder ->
                                                                        builder.claim(
                                                                                "sub",
                                                                                USER_UUID
                                                                                        .toString()))
                                                        .authorities(
                                                                new SimpleGrantedAuthority(
                                                                        "SCOPE_drinkwater:v1:user:profile:update")))
                                        .content(
                                                this.objectMapper.writeValueAsString(
                                                        UPDATE_USER_DTO))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .accept(MediaType.APPLICATION_JSON)
                                        .header("Accept-Language", "en-US"))
                        .andExpect(status().isOk());

        expectUserResponse(result);
    }

    @Test
    void givenValidJwtToken_whenDeleteCurrentUser_thenReturnNoContent() throws Exception {
        // Given
        doNothing().when(userService).deleteByPublicId(USER_UUID);

        // When & Then
        mockMvc.perform(
                        delete("/api/v1/users")
                                .with(
                                        jwt().jwt(
                                                        builder ->
                                                                builder.claim(
                                                                        "sub",
                                                                        USER_UUID.toString()))
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "SCOPE_drinkwater:v1:user:profile:delete")))
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteByPublicId(USER_UUID);
    }

    @Test
    void givenExistingUser_whenCreateUser_thenThrowUserAlreadyExistsException() throws Exception {
        // Given
        when(userService.createUser(USER_UUID, USER_DTO))
                .thenThrow(UserAlreadyExistsException.class);

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
                                .content(this.objectMapper.writeValueAsString(USER_DTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(status().isConflict())
                .andExpect(
                        jsonPath("$.type")
                                .value("https://www.drinkwater.com.br/user-already-exists"))
                .andExpect(jsonPath("$.title").value(HttpStatus.CONFLICT.getReasonPhrase()))
                .andExpect(
                        jsonPath("$.detail")
                                .value(
                                        this.messageSource.getMessage(
                                                "exception.user.already-exists",
                                                null,
                                                Locale.of("en", "US"))))
                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.instance").value("/api/v1/users"));
    }

    @Test
    void givenValidJwtToken_whenGetCurrentUser_thenThrowUserNotFoundException() throws Exception {
        // Given
        when(userService.getUserByPublicId(USER_UUID)).thenThrow(UserNotFoundException.class);

        // When & Then
        mockMvc.perform(
                        get("/api/v1/users/me")
                                .with(
                                        jwt().jwt(
                                                        builder ->
                                                                builder.claim(
                                                                        "sub",
                                                                        USER_UUID.toString()))
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "SCOPE_drinkwater:v1:user:profile:read")))
                                .header("Accept-Language", "en-US"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("https://www.drinkwater.com.br/user-not-found"))
                .andExpect(jsonPath("$.title").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(
                        jsonPath("$.detail")
                                .value(
                                        this.messageSource.getMessage(
                                                "exception.user.not-found",
                                                null,
                                                Locale.of("en", "US"))))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.instance").value("/api/v1/users/me"));
    }

    @Test
    void givenJwtWithoutRequiredScope_whenGetCurrentUser_thenReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(
                        get("/api/v1/users/me")
                                .with(
                                        jwt().jwt(
                                                        builder ->
                                                                builder.claim(
                                                                        "sub",
                                                                        USER_UUID.toString())))
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenJwtWithoutRequiredScope_whenCreateUser_thenReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(
                        post("/api/v1/users")
                                .with(
                                        jwt().jwt(
                                                        builder ->
                                                                builder.claim(
                                                                        "sub",
                                                                        USER_UUID.toString())))
                                .content(this.objectMapper.writeValueAsString(USER_DTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenJwtWithWrongScope_whenDeleteCurrentUser_thenReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(
                        delete("/api/v1/users")
                                .with(
                                        jwt().jwt(
                                                        builder ->
                                                                builder.claim(
                                                                        "sub",
                                                                        USER_UUID.toString()))
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "SCOPE_drinkwater:v1:user:profile:read")))
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(status().isForbidden());
    }

    private void expectUserResponse(ResultActions result) throws Exception {
        result.andExpect(jsonPath("$.publicId").value(USER_UUID.toString()))
                .andExpect(jsonPath("$.email").value(USER_RESPONSE_DTO.email()))
                .andExpect(
                        jsonPath("$.personal.firstName")
                                .value(USER_RESPONSE_DTO.personal().firstName()))
                .andExpect(
                        jsonPath("$.personal.lastName")
                                .value(USER_RESPONSE_DTO.personal().lastName()))
                .andExpect(
                        jsonPath("$.physical.weight").value(USER_RESPONSE_DTO.physical().weight()))
                .andExpect(
                        jsonPath("$.physical.height").value(USER_RESPONSE_DTO.physical().height()))
                .andExpect(jsonPath("$.settings.goal").value(USER_RESPONSE_DTO.settings().goal()))
                .andExpect(
                        jsonPath("$.settings.intervalMinutes")
                                .value(USER_RESPONSE_DTO.settings().intervalMinutes()));
    }
}
