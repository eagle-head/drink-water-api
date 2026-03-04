package br.com.drinkwater.hydrationtracking.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

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
final class WaterIntakeApiNoContainersIT {

    @LocalServerPort private int port;

    @Autowired private MockTestAuthProvider authProvider;

    @Autowired private MessageSource messageSource;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Test
    void givenNoToken_whenGetWaterIntake_thenReturnUnauthorized() {

        // When & Then
        given().when().get("/api/v1/users/water-intakes/1").then().statusCode(401);
    }

    @Test
    void givenValidToken_whenGetWaterIntakeById_thenReturnWaterIntake() {

        // Given
        String token = authProvider.getJohnDoeToken();

        // When & Then
        given().header("Authorization", "Bearer " + token)
                .header("Accept-Language", "en-US")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/users/water-intakes/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("volume", equalTo(250))
                .body("volumeUnit", equalTo("ML"))
                .body("dateTimeUTC", notNullValue());
    }

    @Test
    void givenValidToken_whenCreateWaterIntake_thenReturnCreatedWaterIntake() {

        // Given
        String token = authProvider.getJohnDoeToken();

        String requestBody =
                """
                {
                    "dateTimeUTC": "2024-01-15T10:00:00Z",
                    "volume": 350,
                    "volumeUnit": "ML"
                }
                """;

        // When & Then
        given().header("Authorization", "Bearer " + token)
                .header("Accept-Language", "en-US")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when()
                .post("/api/v1/users/water-intakes")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("volume", equalTo(350))
                .body("volumeUnit", equalTo("ML"))
                .body("dateTimeUTC", equalTo("2024-01-15T10:00:00Z"));
    }

    @Test
    void givenValidToken_whenUpdateWaterIntake_thenReturnUpdatedWaterIntake() {

        // Given
        String token = authProvider.getJohnDoeToken();

        String requestBody =
                """
                {
                    "dateTimeUTC": "2024-08-14T10:00:00Z",
                    "volume": 500,
                    "volumeUnit": "ML"
                }
                """;

        // When & Then
        given().header("Authorization", "Bearer " + token)
                .header("Accept-Language", "en-US")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when()
                .put("/api/v1/users/water-intakes/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("volume", equalTo(500))
                .body("volumeUnit", equalTo("ML"));
    }

    @Test
    void givenValidToken_whenDeleteWaterIntake_thenReturnNoContent() {

        // Given
        String token = authProvider.getJohnDoeToken();

        // When & Then
        given().header("Authorization", "Bearer " + token)
                .header("Accept-Language", "en-US")
                .when()
                .delete("/api/v1/users/water-intakes/1")
                .then()
                .statusCode(204);

        // When & Then
        given().header("Authorization", "Bearer " + token)
                .header("Accept-Language", "en-US")
                .when()
                .get("/api/v1/users/water-intakes/1")
                .then()
                .statusCode(404)
                .body("type", equalTo("https://www.drinkwater.com.br/waterintake-not-found"))
                .body("status", equalTo(404));
    }

    @Test
    void givenValidToken_whenSearchWaterIntakes_thenReturnPaginatedResults() {

        // Given
        String token = authProvider.getJohnDoeToken();

        // When & Then
        given().header("Authorization", "Bearer " + token)
                .header("Accept-Language", "en-US")
                .queryParam("startDate", "2024-08-14T00:00:00Z")
                .queryParam("endDate", "2024-08-14T23:59:59Z")
                .queryParam("size", 10)
                .queryParam("sortField", "dateTimeUTC")
                .queryParam("sortDirection", "ASC")
                .when()
                .get("/api/v1/users/water-intakes")
                .then()
                .statusCode(200)
                .body("content", hasSize(5))
                .body("hasNext", equalTo(false))
                .body("pageSize", equalTo(10));
    }

    @Test
    void givenValidTokenAndNonExistentId_whenGetWaterIntake_thenReturnNotFound() {

        // Given
        String token = authProvider.getJohnDoeToken();

        // When & Then
        given().header("Authorization", "Bearer " + token)
                .header("Accept-Language", "en-US")
                .when()
                .get("/api/v1/users/water-intakes/99999")
                .then()
                .statusCode(404)
                .body("type", equalTo("https://www.drinkwater.com.br/waterintake-not-found"))
                .body("title", equalTo("Not Found"))
                .body(
                        "detail",
                        equalTo(
                                messageSource.getMessage(
                                        "exception.water-intake.not-found",
                                        null,
                                        Locale.of("en", "US"))))
                .body("status", equalTo(404));
    }

    @Test
    void givenDuplicateDateTime_whenCreateWaterIntake_thenReturnBadRequest() {

        // Given
        String token = authProvider.getJohnDoeToken();

        String requestBody =
                """
                {
                    "dateTimeUTC": "2024-08-14T10:00:00Z",
                    "volume": 300,
                    "volumeUnit": "ML"
                }
                """;

        // When & Then
        given().header("Authorization", "Bearer " + token)
                .header("Accept-Language", "en-US")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when()
                .post("/api/v1/users/water-intakes")
                .then()
                .statusCode(400)
                .body("type", equalTo("https://www.drinkwater.com.br/time-range-validation-error"))
                .body("status", equalTo(400));
    }
}
