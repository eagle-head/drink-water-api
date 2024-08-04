package br.com.drinkwater.drinkwaterapi.e2e.user;

import static br.com.drinkwater.drinkwaterapi.usermanagement.constants.UserConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import br.com.drinkwater.drinkwaterapi.usermanagement.dto.UserResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.reactive.server.WebTestClient;

@ActiveProfiles("it")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/remove_users.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class UserIT {

    @Autowired
    private WebTestClient webClient;

    @Test
    void createUser_ReturnsCreated() {
        UserResponseDTO sut = webClient.post().uri("/users")
                .bodyValue(USER_CREATE_DTO)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(UserResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertThat(sut).isNotNull();
        assertThat(sut.id()).isNotNull();
        assertThat(sut.email()).isEqualTo(USER_CREATE_DTO.email());
        assertThat(sut.firstName()).isEqualTo(USER_CREATE_DTO.firstName());
        assertThat(sut.lastName()).isEqualTo(USER_CREATE_DTO.lastName());
        assertThat(sut.birthDate()).isEqualTo(USER_CREATE_DTO.birthDate());
        assertThat(sut.biologicalSex()).isEqualTo(USER_CREATE_DTO.biologicalSex());
        assertThat(sut.weight()).isEqualTo(USER_CREATE_DTO.weight());
        assertThat(sut.weightUnit()).isEqualTo(USER_CREATE_DTO.weightUnit());
        assertThat(sut.height()).isEqualTo(USER_CREATE_DTO.height());
        assertThat(sut.heightUnit()).isEqualTo(USER_CREATE_DTO.heightUnit());
    }

    @Test
    void createUser_WithInvalidData_ReturnsValidationErrors() {
        webClient.post().uri("/users")
                .bodyValue(USER_CREATE_DTO_WITH_INVALID_DATA)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.constraints").isArray()
                .jsonPath("$.constraints.length()").isEqualTo(11)
                .jsonPath("$.constraints[?(@.name == 'email')].userMessage")
                .isEqualTo("Email is required.")
                .jsonPath("$.constraints[?(@.name == 'password')].userMessage")
                .value(containsInAnyOrder("Password is required.", "Password must be between 6 and 20 characters long."))
                .jsonPath("$.constraints[?(@.name == 'firstName')].userMessage")
                .isEqualTo("First name is required.")
                .jsonPath("$.constraints[?(@.name == 'lastName')].userMessage")
                .isEqualTo("Last name is required.")
                .jsonPath("$.constraints[?(@.name == 'birthDate')].userMessage")
                .isEqualTo("Birthdate is required.")
                .jsonPath("$.constraints[?(@.name == 'biologicalSex')].userMessage")
                .isEqualTo("Biological sex is required.")
                .jsonPath("$.constraints[?(@.name == 'weight')].userMessage")
                .isEqualTo("Weight must be a value greater than 45.")
                .jsonPath("$.constraints[?(@.name == 'weightUnit')].userMessage")
                .isEqualTo("Weight unit is required.")
                .jsonPath("$.constraints[?(@.name == 'height')].userMessage")
                .isEqualTo("Height must be a value greater than 100.")
                .jsonPath("$.constraints[?(@.name == 'heightUnit')].userMessage")
                .isEqualTo("Height unit is required.");
    }

    @Test
    void createUser_WithExistingEmail_ReturnsConflict() {
        webClient.post().uri("/users")
                .bodyValue(USER_CREATE_DTO)
                .exchange()
                .expectStatus().isCreated();

        webClient.post().uri("/users")
                .bodyValue(USER_WITH_SAME_EMAIL)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.type").isEqualTo("https://www.drinkwater.com.br/data-conflict")
                .jsonPath("$.title").isEqualTo("Data conflict occurred.")
                .jsonPath("$.detail").isEqualTo("The email provided is already in use.")
                .jsonPath("$.userMessage").isEqualTo("The email provided is already in use.");
    }
}
