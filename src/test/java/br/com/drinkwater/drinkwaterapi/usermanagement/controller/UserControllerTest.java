package br.com.drinkwater.drinkwaterapi.usermanagement.controller;

import static br.com.drinkwater.drinkwaterapi.usermanagement.constants.UserConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import br.com.drinkwater.drinkwaterapi.usermanagement.exception.EmailAlreadyUsedException;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.User;
import br.com.drinkwater.drinkwaterapi.usermanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    public void createUser_WithValidData_ReturnsCreated() throws Exception {
        when(userService.create(USER)).thenReturn(USER);

        mockMvc
                .perform(post("/users")
                        .content(objectMapper.writeValueAsString(USER))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(USER.getEmail()))
                .andExpect(jsonPath("$.password").value(USER.getPassword()))
                .andExpect(jsonPath("$.firstName").value(USER.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(USER.getLastName()))
                .andExpect(jsonPath("$.birthDate").value(USER.getBirthDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"))))
                .andExpect(jsonPath("$.biologicalSex").value(USER.getBiologicalSex().toString()))
                .andExpect(jsonPath("$.weight").value(USER.getWeight()))
                .andExpect(jsonPath("$.weightUnit").value(USER.getWeightUnit().toString()))
                .andExpect(jsonPath("$.height").value(USER.getHeight()))
                .andExpect(jsonPath("$.heightUnit").value(USER.getHeightUnit().toString()));

    }

    @ParameterizedTest
    @MethodSource("provideInvalidUsers")
    public void createUser_WithInvalidData_ReturnsUnprocessableEntity(User invalidUser) throws Exception {
        mockMvc
                .perform(post("/users")
                        .content(objectMapper.writeValueAsString(invalidUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    private static Stream<User> provideInvalidUsers() {
        return Stream.of(
                USER_WITH_EMPTY_EMAIL,
                USER_WITH_NULL_EMAIL,
                USER_WITH_INVALID_EMAIL,
                USER_WITH_INVALID_DATA
        );
    }

    @Test
    public void createUser_WithExistingUser_ReturnsConflict() throws Exception {
        when(userService.create(any())).thenThrow(EmailAlreadyUsedException.class);

        mockMvc
                .perform(post("/users")
                        .content(objectMapper.writeValueAsString(USER))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }
}
