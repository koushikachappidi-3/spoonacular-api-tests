package com.example.api;

import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class GetRecipeInfoTest {
    static final String BASE = "https://api.spoonacular.com";
    static String KEY;

    @BeforeAll
    static void setup() {
        KEY = System.getenv("SPOONACULAR_API_KEY");
        Assumptions.assumeTrue(KEY != null && !KEY.isBlank(), "Set SPOONACULAR_API_KEY first");
    }

    // --- Positive Scenarios (6 Tests) ---

    @Test
    void returns200_for_validRecipeId_default() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .pathParam("id", 716429)
                .when()
                .get("/recipes/{id}/information")
                .then()
                .statusCode(200)
                .body("id", equalTo(716429))
                .body("nutrition", nullValue());
    }

    @Test
    void returns200_withNutrition_when_includeNutrition_isTrue() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("includeNutrition", true)
                .pathParam("id", 716429)
                .when()
                .get("/recipes/{id}/information")
                .then()
                .statusCode(200)
                .body("id", equalTo(716429))
                .body("nutrition", notNullValue());
    }

    @Test
    void returns200_withoutNutrition_when_includeNutrition_isFalse() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("includeNutrition", false)
                .pathParam("id", 716429)
                .when()
                .get("/recipes/{id}/information")
                .then()
                .statusCode(200)
                .body("id", equalTo(716429))
                .body("nutrition", nullValue());
    }

    @Test
    void returns200_withWinePairing_when_addWinePairing_isTrue() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("addWinePairing", true)
                .pathParam("id", 716429)
                .when()
                .get("/recipes/{id}/information")
                .then()
                .statusCode(200)
                .body("winePairing", notNullValue());
    }

    @Test
    void returns200_withTasteData_when_addTasteData_isTrue() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("addTasteData", true)
                .pathParam("id", 716429)
                .when()
                .get("/recipes/{id}/information")
                .then()
                .statusCode(200)
                .body("taste", notNullValue());
    }

    @Test
    void returns200_when_invalidNutritionParam_isIgnored() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("includeNutrition", "banana") // Invalid boolean value
                .pathParam("id", 716429)
                .when()
                .get("/recipes/{id}/information")
                .then()
                .statusCode(200)
                .body("nutrition", nullValue());
    }


    // --- Negative Scenarios (5 Tests) ---

    @Test
    void returns401_for_invalidApiKey() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", "THIS_IS_A_BAD_KEY")
                .pathParam("id", 716429)
                .when()
                .get("/recipes/{id}/information")
                .then()
                .statusCode(401);
    }

    @Test
    void returns404_for_nonExistentRecipeId() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .pathParam("id", -1) // A non-existent ID
                .when()
                .get("/recipes/{id}/information")
                .then()
                .statusCode(404);
    }

    @Test
    void returns404_for_invalidRecipeIdFormat() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .pathParam("id", "banana") // An invalid format for an ID
                .when()
                .get("/recipes/{id}/information")
                .then()
                .statusCode(404);
    }

    /**
     * SCENARIO 10 (Negative): Authentication
     * Checks that a request with a *missing* API key is rejected with 401.
     */
    @Test
    void returns401_for_missingApiKey() {
        given()
                .baseUri(BASE)
                // We are NOT adding the apiKey queryParam
                .pathParam("id", 716429)
                .when()
                .get("/recipes/{id}/information")
                .then()
                .statusCode(401);
    }

    /**
     * SCENARIO 11 (Negative): Not Found
     * Checks that a request to a non-existent sub-endpoint is rejected with 404.
     */
    @Test
    void returns404_for_invalidSubEndpoint() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .pathParam("id", 716429)
                .when()
                .get("/recipes/{id}/this-is-not-real") // Invalid URL
                .then()
                .statusCode(404);
    }
}
