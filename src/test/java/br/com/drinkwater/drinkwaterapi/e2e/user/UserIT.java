package br.com.drinkwater.drinkwaterapi.e2e.user;

import static br.com.drinkwater.drinkwaterapi.usermanagement.constants.UserConstants.USER_CREATE_DTO;
import static org.assertj.core.api.Assertions.assertThat;

import br.com.drinkwater.drinkwaterapi.usermanagement.dto.UserResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        assertThat(sut.email()).isEqualTo(USER_CREATE_DTO.getEmail());
        assertThat(sut.firstName()).isEqualTo(USER_CREATE_DTO.getFirstName());
        assertThat(sut.lastName()).isEqualTo(USER_CREATE_DTO.getLastName());
        assertThat(sut.birthDate()).isEqualTo(USER_CREATE_DTO.getBirthDate());
        assertThat(sut.biologicalSex()).isEqualTo(USER_CREATE_DTO.getBiologicalSex());
        assertThat(sut.weight()).isEqualTo(USER_CREATE_DTO.getWeight());
        assertThat(sut.weightUnit()).isEqualTo(USER_CREATE_DTO.getWeightUnit());
        assertThat(sut.height()).isEqualTo(USER_CREATE_DTO.getHeight());
        assertThat(sut.heightUnit()).isEqualTo(USER_CREATE_DTO.getHeightUnit());
    }
}
