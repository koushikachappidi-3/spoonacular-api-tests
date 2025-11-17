package com.example.api;

import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class PostMapIngredientsTest {
    static final String BASE = "https://api.spoonacular.com";
    static String KEY;

    @BeforeAll
    static void setup() {
        KEY = System.getenv("SPOONACULAR_API_KEY");
        Assumptions.assumeTrue(KEY != null && !KEY.isBlank(), "Set SPOONACULAR_API_KEY first");
    }

    // --- Positive Test cases  ---

    /**
     * Test case 1 : (One Ingredient)
     */
    @Test
    void returns200_and_maps_oneIngredient() {
        String requestBody = "{\n" +
                "  \"ingredients\": [\"flour\"],\n" +
                "  \"servings\": 1\n" +
                "}";

        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/ingredients/map")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].original", equalTo("flour"));
    }

    /**
     * Test case 2 :(Multiple Ingredients)
     */
    @Test
    void returns200_and_maps_multipleIngredients() {
        String requestBody = "{\n" +
                "  \"ingredients\": [\"1 cup flour\", \"2 eggs\"],\n" +
                "  \"servings\": 2\n" +
                "}";

        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/ingredients/map")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].original", equalTo("1 cup flour"))
                .body("[1].original", equalTo("2 eggs"));
    }

    /**
     * Test case 3 : Permissive API (Empty Ingredients Array)
     */
    @Test
    void returns200_and_emptyArray_for_emptyIngredients() {
        String requestBody = "{\n" +
                "  \"ingredients\": [],\n" + // Empty array
                "  \"servings\": 1\n" +
                "}";

        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/ingredients/map")
                .then()
                .statusCode(200)
                .body("$", hasSize(0)); 
    }

    /**
     * Test case  4: Permissive API (Missing 'servings' field)
     */
    @Test
    void returns200_when_servingsField_isMissing() {
        String requestBody = "{\"ingredients\": [\"flour\"]}"; // Missing 'servings'
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/ingredients/map")
                .then()
                .statusCode(200)
                .body("$", hasSize(1));
    }
    
    /**
     * Test case 5 : Permissive API (Invalid 'servings' format)
     * FIX: API ignores "two", returns 200.
     */
    @Test
    void returns200_when_invalidServingsFormat_isIgnored() {
        // 'servings' must be a number, not a string
        String requestBody = "{\"ingredients\": [\"flour\"], \"servings\": \"two\"}";
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/ingredients/map")
                .then()
                .statusCode(200);
    }


    // --- Negative Test cases ---

    /**
     * Test case 6 : Authentication (Invalid Key)
     */
    @Test
    void returns401_for_invalidApiKey() {
        String requestBody = "{\"ingredients\": [\"flour\"], \"servings\": 1}";
        given()
                .baseUri(BASE)
                .queryParam("apiKey", "THIS_IS_A_BAD_KEY")
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/ingredients/map")
                .then()
                .statusCode(401);
    }
    
    /**
     * Test case 7 : Authentication (Missing Key)
     */
    @Test
    void returns401_for_missingApiKey() {
        String requestBody = "{\"ingredients\": [\"flour\"], \"servings\": 1}";
        given()
                .baseUri(BASE)
                // No apiKey param
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/ingredients/map")
                .then()
                .statusCode(401);
    }
    
    /**
     * Test case 8 : Authentication (Empty Key)
     */
    @Test
    void returns401_for_emptyApiKey() {
        String requestBody = "{\"ingredients\": [\"flour\"], \"servings\": 1}";
        given()
                .baseUri(BASE)
                .queryParam("apiKey", "") // Empty key
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/ingredients/map")
                .then()
                .statusCode(401);
    }

    /**
     * Test case 9 : Bad Request (Missing 'ingredients' field)
     */
    @Test
    void returns500_for_missingIngredientsField() {
        String requestBody = "{\"servings\": 1}"; // Missing 'ingredients'
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/ingredients/map")
                .then()
                .statusCode(500);
    }

    /**
     * Test case 10 : Bad Request (Invalid JSON Syntax)
     */
    @Test
    void returns500_for_invalidJsonSyntax() {
        String requestBody = "{\"ingredients\": [\"flour\"], \"servings\": 1"; // Missing closing }
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/ingredients/map")
                .then()
                .statusCode(500);
    }
    
    /**
     * Test case 11 : Bad Request (Invalid 'ingredients' format)
     */
    @Test
    void returns500_for_invalidIngredientsFormat() {
        // 'ingredients' must be an array, not a string
        String requestBody = "{\"ingredients\": \"flour\", \"servings\": 1}";
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/ingredients/map")
                .then()
                .statusCode(500); 
    }
    
    /**
     * Test case 12 : Not Found (Invalid Endpoint)
     */
    @Test
    void returns404_for_invalidEndpoint() {
        String requestBody = "{\"ingredients\": [\"flour\"], \"servings\": 1}";
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/ingredients/map/bad-path")
                .then()
                .statusCode(404);
    }
}
