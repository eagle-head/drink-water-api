package br.com.drinkwater.usermanagement.integration;

import static br.com.drinkwater.usermanagement.constants.UserTestConstants.*;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

import br.com.drinkwater.config.MockContainersConfig;
import br.com.drinkwater.support.MockTestAuthProvider;
import io.restassured.RestAssured;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

/**
 * Integration tests for User API without Docker containers. Uses H2 in-memory database and mocked
 * JWT decoder for environments where Docker is not available.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(MockContainersConfig.class)
@ActiveProfiles("it-no-containers")
@SqlGroup({
    @Sql(
            scripts = {"/reset-test-data.sql", "/insert-test-data.sql"},
            executionPhase = BEFORE_TEST_METHOD),
    @Sql(
            scripts = {"/reset-test-data.sql"},
            executionPhase = AFTER_TEST_METHOD)
})
final class UserApiNoContainersIT {

    @LocalServerPort private int port;

    @Autowired private MockTestAuthProvider authProvider;

    @Autowired private JwtDecoder jwtDecoder;

    @Autowired private MessageSource messageSource;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Test
    void givenNoToken_whenGetUserInfo_thenReturnUnauthorized() {

        // When & Then
        when().get("/api/v1/users/me").then().statusCode(401);
    }

    @Test
    void givenValidToken_whenGetUserInfo_thenReturnUserDetails() {

        // Given
        String token = authProvider.getJohnDoeToken();
        String publicId = jwtDecoder.decode(token).getSubject();

        // When & Then
        given().header("Authorization", "Bearer " + token)
                .header("Accept-Language", "en-US")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/users/me")
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
                .body("settings.dailyStartTime", equalTo("08:00:00"))
                .body("settings.dailyEndTime", equalTo("22:00:00"));
    }

    @Test
    void givenValidToken_whenCreateUser_thenReturnCreatedUser() {

        // Given
        String token = authProvider.getNewUserToken();
        String publicId = jwtDecoder.decode(token).getSubject();

        // When & Then
        given().header("Authorization", "Bearer " + token)
                .header("Accept-Language", "en-US")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(USER_DTO)
                .when()
                .post("/api/v1/users")
                .then()
                .statusCode(201)
                .body("publicId", equalTo(publicId))
                .body("email", equalTo(USER_DTO.email()))
                .body("personal.firstName", equalTo(USER_DTO.personal().firstName()))
                .body("personal.lastName", equalTo(USER_DTO.personal().lastName()))
                .body("personal.birthDate", equalTo("1990-01-01"))
                .body(
                        "personal.biologicalSex",
                        equalTo(USER_DTO.personal().biologicalSex().toString()))
                .body("physical.weight", equalTo(USER_DTO.physical().weight().floatValue()))
                .body("physical.weightUnit", equalTo(USER_DTO.physical().weightUnit().toString()))
                .body("physical.height", equalTo(USER_DTO.physical().height().floatValue()))
                .body("physical.heightUnit", equalTo(USER_DTO.physical().heightUnit().toString()))
                .body("settings.goal", equalTo(USER_DTO.settings().goal()))
                .body("settings.intervalMinutes", equalTo(USER_DTO.settings().intervalMinutes()))
                .body("settings.dailyStartTime", equalTo("08:00:00"))
                .body("settings.dailyEndTime", equalTo("22:00:00"));
    }

    @Test
    void givenValidToken_whenUpdateUser_thenReturnUpdatedUser() {

        // Given
        String token = authProvider.getJohnDoeToken();

        String requestBody =
                """
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
                        "dailyStartTime": "08:00:00",
                        "dailyEndTime": "22:00:00"
                      }
                    }
                """;

        // When & Then
        given().header("Authorization", "Bearer " + token)
                .header("Accept-Language", "en-US")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when()
                .put("/api/v1/users")
                .then()
                .statusCode(200)
                .body("personal.lastName", equalTo("Updated"))
                .body("physical.weight", equalTo(125.8F))
                .body("physical.height", equalTo(191.0F))
                .body("settings.intervalMinutes", equalTo(15));
    }

    @Test
    void givenValidToken_whenDeleteUser_thenReturnNoContent() {

        // Given
        String token = authProvider.getJohnDoeToken();

        // When & Then
        given().header("Authorization", "Bearer " + token)
                .header("Accept-Language", "en-US")
                .when()
                .delete("/api/v1/users")
                .then()
                .statusCode(204);

        // When & Then
        given().header("Authorization", "Bearer " + token)
                .header("Accept-Language", "en-US")
                .when()
                .get("/api/v1/users/me")
                .then()
                .statusCode(404)
                .contentType(containsString("application/problem+json"))
                .body("type", equalTo("https://www.drinkwater.com.br/user-not-found"))
                .body("title", equalTo("Not Found"))
                .body(
                        "detail",
                        equalTo(
                                messageSource.getMessage(
                                        "exception.user.not-found", null, Locale.of("en", "US"))))
                .body("status", equalTo(404))
                .body("instance", equalTo("/api/v1/users/me"));
    }
}
