package com.example.api;

import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class GetSimilarRecipesTest {
    static final String BASE = "https://api.spoonacular.com";
    static String KEY;

    @BeforeAll
    static void setup() {
        KEY = System.getenv("SPOONACULAR_API_KEY");
        Assumptions.assumeTrue(KEY != null && !KEY.isBlank(), "Set SPOONACULAR_API_KEY first");
    }

    // --- Positive Test Cases  ---

    /**
     * Test Case 1
     */
    @Test
    void returns200_for_validRecipeId_default() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .pathParam("id", 716429)
                .when()
                .get("/recipes/{id}/similar")
                .then()
                .statusCode(200)
                .body("$", not(empty()));
    }

    /**
     * Test Case 2: With 'number=1'
     */
    @Test
    void returns200_and_oneRecipe_with_numberOne() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("number", 1)
                .pathParam("id", 716429)
                .when()
                .get("/recipes/{id}/similar")
                .then()
                .statusCode(200)
                .body("$", hasSize(1));
    }

    /**
     * Test Case 3 : With 'number=2'
     */
    @Test
    void returns200_and_correctCount_with_numberParam() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("number", 2)
                .pathParam("id", 716429)
                .when()
                .get("/recipes/{id}/similar")
                .then()
                .statusCode(200)
                .body("$", hasSize(2));
    }

    /**
     * Test Case 4 : With 'number=100' (Max)
     */
    @Test
    void returns200_and_maxRecipes_with_numberMax() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("number", 100)
                .pathParam("id", 716429)
                .when()
                .get("/recipes/{id}/similar")
                .then()
                .statusCode(200)
                .body("$", hasSize(lessThanOrEqualTo(100)));
    }

    /**
     * Test Case  5 : Edge Case (number=0)
     */
    @Test
    void returns200_when_numberZero_isIgnored() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("number", 0)
                .pathParam("id", 716429)
                .when()
                .get("/recipes/{id}/similar")
                .then()
                .statusCode(200)
                .body("$", not(empty()));
    }

    /**
     * Test Case 6 : Data Validation
     */
    @Test
    void returns200_and_validDataFields() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("number", 1)
                .pathParam("id", 716429)
                .when()
                .get("/recipes/{id}/similar")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", notNullValue())
                .body("[0].title", not(emptyOrNullString()));
    }

    /**
     * Test Case 7 : Permissive API (Out of Range High)
     */
    @Test
    void returns200_when_number_outOfRange_isIgnored() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("number", 101)
                .pathParam("id", 716429)
                .when()
                .get("/recipes/{id}/similar")
                .then()
                .statusCode(200)
                .body("$", not(empty()));
    }

    /**
     * Test Case 8 : Permissive API (Non-existent ID)
     */
    @Test
    void returns200_and_emptyList_for_nonExistentRecipeId() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .pathParam("id", -1)
                .when()
                .get("/recipes/{id}/similar")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    // --- Negative Test Cases  ---

    /**
     * Test Case 9 : Authentication (Invalid Key)
     */
    @Test
    void returns401_for_invalidApiKey() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", "THIS_IS_A_BAD_KEY")
                .pathParam("id", 716429)
                .when()
                .get("/recipes/{id}/similar")
                .then()
                .statusCode(401); 
    }

    /**
     * Test Case 10 : Authentication (Missing Key)
     */
    @Test
    void returns401_for_missingApiKey() {
        given()
                .baseUri(BASE)
                // No apiKey param
                .pathParam("id", 716429)
                .when()
                .get("/recipes/{id}/similar")
                .then()
                .statusCode(401); 
    }

    /**
     * Test Case 11 : Not Found (Invalid ID Format)
     */
    @Test
    void returns404_for_invalidRecipeIdFormat() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .pathParam("id", "banana")
                .when()
                .get("/recipes/{id}/similar")
                .then()
                .statusCode(404);
    }

    /**
     * Test Case 12 : Not Found (Invalid Param Format)
     */
    @Test
    void returns404_for_number_invalidFormat() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("number", "apple")
                .pathParam("id", 716429)
                .when()
                .get("/recipes/{id}/similar")
                .then()
                .statusCode(404);
    }

    /**
     * Test Case 13 : Not Found (Invalid Endpoint)
     */
    @Test
    void returns404_for_invalidSubEndpoint() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .pathParam("id", 716429)
                .when()
                .get("/recipes/{id}/similar/this-is-not-real")
                .then()
                .statusCode(404);
    }
}
