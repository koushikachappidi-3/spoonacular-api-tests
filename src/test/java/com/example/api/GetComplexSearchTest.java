package com.example.api;

import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class GetComplexSearchTest {
    static final String BASE = "https://api.spoonacular.com";
    static String KEY;

    @BeforeAll
    static void setup() {
        KEY = System.getenv("SPOONACULAR_API_KEY");
        Assumptions.assumeTrue(KEY != null && !KEY.isBlank(), "Set SPOONACULAR_API_KEY first");
    }

    // --- Positive Test Case ---

    /**
     * Test Case 1 
     */
    @Test
    void returns200_for_validQuery() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("query", "pasta")
                .queryParam("number", 2)
                .when()
                .get("/recipes/complexSearch")
                .then()
                .statusCode(200)
                .body("results", hasSize(2));
    }

    /**
     * Test Case 2 : Edge Case
     */
    @Test
    void returns200_and_emptyResults_for_nonsenseQuery() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("query", "asdfjklasdfjklasdfjkl")
                .queryParam("number", 2)
                .when()
                .get("/recipes/complexSearch")
                .then()
                .statusCode(200)
                .body("results", hasSize(0));
    }

    /**
     * Test Case 3 : Parameter Combination
     */
    @Test
    void returns200_for_query_withCuisine() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("query", "pasta")
                .queryParam("cuisine", "italian")
                .queryParam("number", 1)
                .when()
                .get("/recipes/complexSearch")
                .then()
                .statusCode(200)
                .body("results", hasSize(1));
    }

   /**
     * Test Case 4: Parameter Combination
     */
    @Test
    void returns200_for_query_withMaxReadyTime() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("query", "pizza")
                .queryParam("maxReadyTime", 30)
                .queryParam("number", 1)
                .when()
                .get("/recipes/complexSearch")
                .then()
                .statusCode(200);
    }
    
    /**
     * Test Case 5 : Permissive API (Invalid 'ranking' format)
     */
    @Test
    void returns200_when_invalidRankingFormat_isIgnored() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("query", "pasta")
                .queryParam("ranking", "banana") // Invalid format
                .when()
                .get("/recipes/complexSearch")
                .then()
                .statusCode(200); // **FIXED ASSERTION**
    }

    // --- Negative Test Case  ---

    /**
     * Test Case 6 : Not Found
     */
    @Test
    void returns404_for_invalidEndpoint() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .when()
                .get("/recipes/THIS_ENDPOINT_DOES_NOT_EXIST")
                .then()
                .statusCode(404);
    }
    
    /**
     * Test Case 7 : Authentication (Missing Key)
     */
    @Test
    void returns401_for_missingApiKey() {
        given()
                .baseUri(BASE)
                // No apiKey param
                .queryParam("query", "pasta")
                .when()
                .get("/recipes/complexSearch")
                .then()
                .statusCode(401);
    }
    
    /**
     * Test Case 8 : Authentication (Empty Key)
     */
    @Test
    void returns401_for_emptyApiKey() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", "") 
                .queryParam("query", "pasta")
                .when()
                .get("/recipes/complexSearch")
                .then()
                .statusCode(401);
    }
    
    /**
     * Test Case 9 : Bad Request (Invalid 'number' format)
     */
    @Test
    void returns404_for_invalidNumberFormat() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("query", "pasta")
                .queryParam("number", "two") 
                .when()
                .get("/recipes/complexSearch")
                .then()
                .statusCode(404); 
    }
    
    /**
     * Test Case 10 : Authentication (Invalid Key)
     */
    @Test
    void returns401_for_invalidApiKey() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", "THIS_IS_A_BAD_KEY")
                .queryParam("query", "pasta")
                .when()
                .get("/recipes/complexSearch")
                .then()
                .statusCode(401); 
    }
}
