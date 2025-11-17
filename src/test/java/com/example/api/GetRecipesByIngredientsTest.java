package com.example.api;

import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class GetRecipesByIngredientsTest {
    static final String BASE = "https://api.spoonacular.com";
    static String KEY;

    @BeforeAll
    static void setup() {
        KEY = System.getenv("SPOONACULAR_API_KEY");
        Assumptions.assumeTrue(KEY != null && !KEY.isBlank(), "Set SPOONACULAR_API_KEY first");
    }

    // --- Positive Scenarios (13 Tests) ---

    /**
     * SCENARIO 1 (Positive): Happy Path (One Ingredient)
     */
    @Test
    void returns200_for_oneIngredient() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("ingredients", "apples")
                .queryParam("number", 2)
                .when()
                .get("/recipes/findByIngredients")
                .then()
                .statusCode(200)
                .body("$", hasSize(2));
    }

    /**
     * SCENARIO 2 (Positive): Happy Path (Multiple Ingredients)
     */
    @Test
    void returns200_for_multipleIngredients() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("ingredients", "apples,+flour,+sugar") // Comma-separated
                .queryParam("number", 1)
                .when()
                .get("/recipes/findByIngredients")
                .then()
                .statusCode(200)
                .body("$", hasSize(1));
    }

    /**
     * SCENARIO 3 (Positive): With 'ranking' Parameter
     */
    @Test
    void returns200_with_rankingParam() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("ingredients", "apples,+flour")
                .queryParam("number", 1)
                .queryParam("ranking", 1) // Maximize used ingredients
                .when()
                .get("/recipes/findByIngredients")
                .then()
                .statusCode(200)
                .body("$", hasSize(1));
    }

    /**
     * SCENARIO 4 (Positive): Data Validation
     */
    @Test
    void returns200_and_validDataFields() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("ingredients", "apples")
                .queryParam("number", 1)
                .when()
                .get("/recipes/findByIngredients")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", notNullValue())
                .body("[0].title", not(emptyOrNullString()))
                .body("[0].missedIngredientCount", notNullValue());
    }

    /**
     * SCENARIO 5 (Positive): Permissive API (Invalid Ranking)
     */
    @Test
    void returns200_when_invalidRanking_isIgnored() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("ingredients", "apples")
                .queryParam("ranking", 3) // Only 1 or 2 are allowed
                .when()
                .get("/recipes/findByIngredients")
                .then()
                .statusCode(200)
                .body("$", not(empty()));
    }
    
    /**
     * SCENARIO 6 (Positive): Permissive API (Missing Ingredients)
     */
    @Test
    void returns200_and_emptyList_for_missingIngredientsParam() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                // No 'ingredients' param
                .queryParam("number", 2)
                .when()
                .get("/recipes/findByIngredients")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }
    
    /**
     * SCENARIO 7 (Positive): Permissive API (Number=0)
     */
    @Test
    void returns200_when_numberZero_isIgnored() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("ingredients", "apples")
                .queryParam("number", 0) // 0 is not allowed
                .when()
                .get("/recipes/findByIngredients")
                .then()
                .statusCode(200)
                .body("$", not(empty()));
    }
    
    /**
     * SCENARIO 8 (Positive): Edge Case (No Results)
     */
    @Test
    void returns200_and_emptyList_for_nonsenseIngredient() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("ingredients", "asdfjklasdfjkl")
                .queryParam("number", 5)
                .when()
                .get("/recipes/findByIngredients")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    /**
     * SCENARIO 9 (Positive): Permissive API (Invalid 'limitLicense')
     */
    @Test
    void returns200_when_invalidLimitLicense_isIgnored() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("ingredients", "apples")
                .queryParam("limitLicense", "banana") // Invalid boolean
                .when()
                .get("/recipes/findByIngredients")
                .then()
                .statusCode(200);
    }
    
    /**
     * SCENARIO 10 (Positive): Permissive API (Invalid 'ignorePantry')
     */
    @Test
    void returns200_when_invalidIgnorePantry_isIgnored() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("ingredients", "apples")
                .queryParam("ignorePantry", "banana") // Invalid boolean
                .when()
                .get("/recipes/findByIngredients")
                .then()
                .statusCode(200);
    }
    
    /**
     * SCENARIO 11 (Positive): Permissive API (Invalid 'number' range high)
     */
    @Test
    void returns200_when_number_outOfRange_isIgnored() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("ingredients", "apples")
                .queryParam("number", 101) // > 100
                .when()
                .get("/recipes/findByIngredients")
                .then()
                .statusCode(200);
    }
    
    /**
     * SCENARIO 12 (Positive): Permissive API (Empty 'ingredients' string)
     */
    @Test
    void returns200_and_emptyList_for_emptyIngredientsString() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("ingredients", "") // Empty string
                .when()
                .get("/recipes/findByIngredients")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }
    
    /**
     * SCENARIO 13 (Positive): Permissive API (Negative 'number')
     */
    @Test
    void returns200_when_negativeNumber_isIgnored() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("ingredients", "apples")
                .queryParam("number", -1) // Negative number
                .when()
                .get("/recipes/findByIngredients")
                .then()
                .statusCode(200)
                .body("$", not(empty()));
    }

    // --- Negative Scenarios (8 Tests) ---

    /**
     * SCENARIO 14 (Negative): Authentication (Invalid Key)
     */
    @Test
    void returns401_for_invalidApiKey() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", "THIS_IS_A_BAD_KEY")
                .queryParam("ingredients", "apples")
                .when()
                .get("/recipes/findByIngredients")
                .then()
                .statusCode(401);
    }

    /**
     * SCENARIO 15 (Negative): Authentication (Missing Key)
     */
    @Test
    void returns401_for_missingApiKey() {
        given()
                .baseUri(BASE)
                // No apiKey param
                .queryParam("ingredients", "apples")
                .when()
                .get("/recipes/findByIngredients")
                .then()
                .statusCode(401);
    }
    
    /**
     * SCENARIO 16 (Negative): Authentication (Empty Key)
     */
    @Test
    void returns401_for_emptyApiKey() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", "") // Empty key
                .queryParam("ingredients", "apples")
                .when()
                .get("/recipes/findByIngredients")
                .then()
                .statusCode(401);
    }

    /**
     * SCENARIO 17 (Negative): Not Found (Invalid 'number' format)
     */
    @Test
    void returns404_for_invalidNumberFormat_string() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("ingredients", "apples")
                .queryParam("number", "two") // Invalid format
                .when()
                .get("/recipes/findByIngredients")
                .then()
                .statusCode(404);
    }
    
    /**
     * SCENARIO 18 (Negative): Not Found (Invalid 'number' format - float)
     */
    @Test
    void returns404_for_invalidNumberFormat_float() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("ingredients", "apples")
                .queryParam("number", 1.5) // Invalid format (must be integer)
                .when()
                .get("/recipes/findByIngredients")
                .then()
                .statusCode(404);
    }
    
    /**
     * SCENARIO 19 (Negative): Not Found (Invalid 'ranking' format - float)
     */
    @Test
    void returns404_for_invalidRankingFormat_float() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("ingredients", "apples")
                .queryParam("ranking", 1.5) // Invalid format (must be 1 or 2)
                .when()
                .get("/recipes/findByIngredients")
                .then()
                .statusCode(404);
    }

    /**
     * SCENARIO 20 (Negative): Not Found (Invalid 'ranking' format - string)
     */
    @Test
    void returns404_for_invalidRankingFormat_string() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("ingredients", "apples")
                .queryParam("ranking", "apple") // Invalid format
                .when()
                .get("/recipes/findByIngredients")
                .then()
                .statusCode(404);
    }
    
    /**
     * SCENARIO 21 (Negative): Not Found (Invalid Endpoint)
     */
    @Test
    void returns404_for_invalidEndpoint() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("ingredients", "apples")
                .when()
                .get("/recipes/findByIngredients/bad-path")
                .then()
                .statusCode(404);
    }
}
