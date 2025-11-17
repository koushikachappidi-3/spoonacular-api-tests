package com.example.api;

import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class PostClassifyCuisineTest {
    static final String BASE = "https://api.spoonacular.com";
    static String KEY;

    @BeforeAll
    static void setup() {
        KEY = System.getenv("SPOONACULAR_API_KEY");
        Assumptions.assumeTrue(KEY != null && !KEY.isBlank(), "Set SPOONACULAR_API_KEY first");
    }

    // --- Positive Test cases  ---

    /**
     * Test case 1
     */
    @Test
    void returns200_and_classifies_italian() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "Spaghetti Carbonara")
                .formParam("ingredientList", "pasta, eggs, pancetta, cheese")
                .when()
                .post("/recipes/cuisine")
                .then()
                .statusCode(200)
                .body("cuisine", equalTo("Mediterranean"));
    }

    /**
     * Test case 
     */
    @Test
    void returns200_and_classifies_mexican() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "Chicken Tacos")
                .formParam("ingredientList", "chicken, tortillas, salsa, cilantro")
                .when()
                .post("/recipes/cuisine")
                .then()
                .statusCode(200)
                .body("cuisine", equalTo("Mexican"));
    }

    /**
     * Test case 3 : Data Validation
     */
    @Test
    void returns200_and_validConfidenceScore() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "Tacos")
                .when()
                .post("/recipes/cuisine")
                .then()
                .statusCode(200)
                .body("cuisine", not(emptyOrNullString()))
                .body("confidence", allOf(greaterThanOrEqualTo(0.0f), lessThanOrEqualTo(1.0f)));
    }
    
    /**
     * Test case 4 : Permissive API (Missing Title)
     */
    @Test
    void returns200_when_title_isMissing() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("ingredientList", "chicken, tortillas") // Missing title
                .when()
                .post("/recipes/cuisine")
                .then()
                .statusCode(200);
    }

    /**
     * Test case 5 : Permissive API (Empty Body)
     */
    @Test
    void returns200_for_emptyBody() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/x-www-form-urlencoded")
                // No form params
                .when()
                .post("/recipes/cuisine")
                .then()
                .statusCode(200);
    }
    
    /**
     * Test case 6 : Permissive API (Invalid Language)
     */
    @Test
    void returns200_when_invalidLanguage_isIgnored() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("language", "xx") // Invalid language
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "Tacos")
                .when()
                .post("/recipes/cuisine")
                .then()
                .statusCode(200);
    }
    
    /**
     * Test case 7 : Permissive API (Invalid Content-Type)
     */
    @Test
    void returns200_when_invalidContentType_isIgnored() {
        String jsonBody = "{\"title\": \"Tacos\"}";
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/json") // **Wrong Content-Type**
                .body(jsonBody)
                .when()
                .post("/recipes/cuisine")
                .then()
                .statusCode(200);
    }

    // --- Negative Test cases ---

    /**
     * Test case 8 : Authentication (Invalid Key)
     */
    @Test
    void returns401_for_invalidApiKey() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", "THIS_IS_A_BAD_KEY")
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "Tacos")
                .when()
                .post("/recipes/cuisine")
                .then()
                .statusCode(401);
    }

   /**
     * Test case 9 : Not Found (Invalid Endpoint)
     */
    @Test
    void returns404_for_invalidEndpoint() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "Tacos")
                .when()
                .post("/recipes/cuisine/bad-path") // Invalid URL
                .then()
                .statusCode(404);
    }
    
    /**
     * Test case 10 : Authentication (Missing Key)
     */
    @Test
    void returns401_for_missingApiKey() { 
        given()
                .baseUri(BASE)
                // No apiKey param
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "Tacos")
                .when()
                .post("/recipes/cuisine")
                .then()
                .statusCode(401);
    }
    
    /**
     * Test case 11 : Authentication (Empty Key)
     */
    @Test
    void returns401_for_emptyApiKey() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", "") // Empty key
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "Tacos")
                .when()
                .post("/recipes/cuisine")
                .then()
                .statusCode(401);
    }
}
