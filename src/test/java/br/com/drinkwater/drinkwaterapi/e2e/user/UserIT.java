package br.com.drinkwater.drinkwaterapi.e2e.user;

import static org.assertj.core.api.Assertions.assertThat;
import static br.com.drinkwater.drinkwaterapi.usermanagement.constants.UserConstants.USER;

import br.com.drinkwater.drinkwaterapi.usermanagement.dto.UserResponseDTO;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.User;
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
                .bodyValue(USER)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(UserResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertThat(sut).isNotNull();
        assertThat(sut.id()).isNotNull();
        assertThat(sut.email()).isEqualTo(USER.getEmail());
        assertThat(sut.firstName()).isEqualTo(USER.getFirstName());
        assertThat(sut.lastName()).isEqualTo(USER.getLastName());
        assertThat(sut.birthDate()).isEqualTo(USER.getBirthDate());
        assertThat(sut.biologicalSex()).isEqualTo(USER.getBiologicalSex());
        assertThat(sut.weight()).isEqualTo(USER.getWeight());
        assertThat(sut.weightUnit()).isEqualTo(USER.getWeightUnit());
        assertThat(sut.height()).isEqualTo(USER.getHeight());
        assertThat(sut.heightUnit()).isEqualTo(USER.getHeightUnit());
    }
}
