package br.com.drinkwater.drinkwaterapi.e2e.user;

import static org.assertj.core.api.Assertions.assertThat;
import static br.com.drinkwater.drinkwaterapi.usermanagement.constants.UserConstants.USER;

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
        User sut = this.webClient.post().uri("/users")
                .bodyValue(USER)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(User.class)
                .returnResult()
                .getResponseBody();

        assertThat(sut).isNotNull();
        assertThat(sut.getId()).isNotNull();
        assertThat(sut.getEmail()).isEqualTo(USER.getEmail());
        assertThat(sut.getPassword()).isEqualTo(USER.getPassword());
        assertThat(sut.getFirstName()).isEqualTo(USER.getFirstName());
        assertThat(sut.getLastName()).isEqualTo(USER.getLastName());
        assertThat(sut.getBirthDate()).isEqualTo(USER.getBirthDate());
        assertThat(sut.getBiologicalSex()).isEqualTo(USER.getBiologicalSex());
        assertThat(sut.getWeight()).isEqualTo(USER.getWeight());
        assertThat(sut.getWeightUnit()).isEqualTo(USER.getWeightUnit());
        assertThat(sut.getHeight()).isEqualTo(USER.getHeight());
        assertThat(sut.getHeightUnit()).isEqualTo(USER.getHeightUnit());
    }
}
