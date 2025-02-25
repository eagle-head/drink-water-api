package br.com.drinkwater.usermanagement.integration;

import br.com.drinkwater.support.TestAuthProvider;
import br.com.drinkwater.config.ContainersConfig;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(ContainersConfig.class)
@ActiveProfiles("it")
@SqlGroup({
        @Sql(scripts = {"/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = {"/reset.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public final class UserApiIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestAuthProvider authProvider;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
    }

    @Test
    public void givenNoToken_whenGetUserInfo_thenReturnUnauthorized() {
        when()
                .get("/users/me")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenValidToken_whenGetUserInfo_thenReturnUserDetails() {
        String token = authProvider.getToken();

        given()
                .header("Authorization", "Bearer " + token)
                .header("Accept-Language", "en-US")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/users/me")
                .then()
                .statusCode(200)
                .body("publicId", equalTo("fbc58717-5d48-4041-9f1c-257e8052428f"))
                .body("email", equalTo("john.doe@test.com"))
                .body("personal.firstName", equalTo("John"))
                .body("personal.lastName", equalTo("Doe"))
                .body("personal.birthDate", equalTo("1990-01-01T00:00:00Z"))
                .body("personal.biologicalSex", equalTo("MALE"))
                .body("physical.weight", equalTo(70.5F))
                .body("physical.weightUnit", equalTo("KG"))
                .body("physical.height", equalTo(175.0F))
                .body("physical.heightUnit", equalTo("CM"))
                .body("settings.goal", equalTo(2000))
                .body("settings.intervalMinutes", equalTo(60))
                .body("settings.dailyStartTime", equalTo("2024-01-01T08:00:00Z"))
                .body("settings.dailyEndTime", equalTo("2024-01-01T22:00:00Z"));
    }
}
