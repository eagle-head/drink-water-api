package br.com.drinkwater.usermanagement.integration;

import br.com.drinkwater.support.TestAuthProvider;
import br.com.drinkwater.config.ContainersConfig;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

import static br.com.drinkwater.usermanagement.constants.UserTestConstants.*;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(ContainersConfig.class)
@ActiveProfiles("it")
@SqlGroup({
        @Sql(scripts = {"/data.sql"}, executionPhase = BEFORE_TEST_METHOD),
        @Sql(scripts = {"/reset.sql"}, executionPhase = AFTER_TEST_METHOD)
})
public final class UserApiIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestAuthProvider authProvider;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private MessageSource messageSource;

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

        String token = authProvider.getJohnDoeToken();
        String publicId = jwtDecoder.decode(token).getSubject();

        given()
                .header("Authorization", "Bearer " + token)
                .header("Accept-Language", "en-US")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/users/me")
                .then()
                .statusCode(200)
                .body("publicId", equalTo(publicId))
                .body("email", equalTo("john.doe@test.com"))
                .body("personal.firstName", equalTo("John"))
                .body("personal.lastName", equalTo("Doe"))
                .body("personal.birthDate", equalTo("1990-01-01"))
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

    @Test
    public void givenValidToken_whenCreateUser_thenReturnCreatedUser() {

        String token = authProvider.getNewUserToken();
        String publicId = jwtDecoder.decode(token).getSubject();

        given()
                .header("Authorization", "Bearer " + token)
                .header("Accept-Language", "en-US")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(USER_DTO)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body("publicId", equalTo(publicId))
                .body("email", equalTo(USER_DTO.email()))
                .body("personal.firstName", equalTo(USER_DTO.personal().firstName()))
                .body("personal.lastName", equalTo(USER_DTO.personal().lastName()))
                .body("personal.birthDate", equalTo("1990-01-01"))
                .body("personal.biologicalSex", equalTo(USER_DTO.personal().biologicalSex().toString()))
                .body("physical.weight", equalTo(USER_DTO.physical().weight().floatValue()))
                .body("physical.weightUnit", equalTo(USER_DTO.physical().weightUnit().toString()))
                .body("physical.height", equalTo(USER_DTO.physical().height().floatValue()))
                .body("physical.heightUnit", equalTo(USER_DTO.physical().heightUnit().toString()))
                .body("settings.goal", equalTo(USER_DTO.settings().goal()))
                .body("settings.intervalMinutes", equalTo(USER_DTO.settings().intervalMinutes()))
                .body("settings.dailyStartTime", equalTo("2024-01-01T08:00:00Z"))
                .body("settings.dailyEndTime", equalTo("2024-01-01T22:00:00Z"));
    }

    @Test
    public void givenValidToken_whenUpdateUser_thenReturnUpdatedUser() {

        String token = authProvider.getJohnDoeToken();

        String requestBody = """
                    {
                      "email": "john.doe@test.com",
                      "personal": {
                        "firstName": "John",
                        "lastName": "Updated",
                        "birthDate": "1990-01-01T00:00:00Z",
                        "biologicalSex": "MALE"
                      },
                      "physical": {
                        "weight": 125.8,
                        "weightUnit": "KG",
                        "height": 191.0,
                        "heightUnit": "CM"
                      },
                      "settings": {
                        "goal": 2000,
                        "intervalMinutes": 15,
                        "dailyStartTime": "2024-01-01T08:00:00Z",
                        "dailyEndTime": "2024-01-01T22:00:00Z"
                      }
                    }
                """;

        given()
                .header("Authorization", "Bearer " + token)
                .header("Accept-Language", "en-US")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when()
                .put("/users")
                .then()
                .statusCode(200)
                .body("personal.lastName", equalTo("Updated"))
                .body("physical.weight", equalTo(125.8F))
                .body("physical.height", equalTo(191.0F))
                .body("settings.intervalMinutes", equalTo(15));
    }

    @Test
    public void givenValidToken_whenDeleteUser_thenReturnNoContent() {

        String token = authProvider.getJohnDoeToken();

        given()
                .header("Authorization", "Bearer " + token)
                .header("Accept-Language", "en-US")
                .when()
                .delete("/users")
                .then()
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + token)
                .header("Accept-Language", "en-US")
                .when()
                .get("/users/me")
                .then()
                .statusCode(404)
                .contentType(containsString("application/problem+json"))
                .body("type", equalTo("https://www.drinkwater.com.br/user-not-found"))
                .body("title", equalTo("Not Found"))
                .body("detail", equalTo(this.messageSource.getMessage(
                        "user.not.found.detail",
                        null,
                        new Locale("en", "US"))
                ))
                .body("status", equalTo(404))
                .body("instance", equalTo("/users/me"));
    }
}