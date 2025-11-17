package com.example.api;

import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class PostClassifyProductTest {
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
    void returns200_and_classifies_byTitle() {
        String requestBody = "{\"title\": \"Kroger Vitamin A & D Reduced Fat 2% Milk\"}";

        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/products/classify")
                .then()
                .statusCode(200)
                .body("category", equalTo("2 percent milk"));
    }

    /**
     * Test case 2 
     */
    @Test
    void returns200_and_classifies_anotherTitle() {
        String requestBody = "{\"title\": \"Skippy Creamy Peanut Butter\"}";

        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/products/classify")
                .then()
                .statusCode(200)
                .body("category", equalTo("creamy peanut butter"));
    }
    
    /**
     * Test case 3  (All Fields)
     */
    @Test
    void returns200_and_classifies_byTitle_when_allFieldsPresent() {
        String requestBody = "{\n" +
                "  \"title\": \"Skippy Creamy Peanut Butter\",\n" +
                "  \"upc\": \"051500255109\",\n" +
                "  \"plu_code\": \"4011\"\n" +
                "}";

        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/products/classify")
                .then()
                .statusCode(200)
                .body("category", equalTo("creamy peanut butter"));
    }

    // --- Negative Test cases  ---
    
    /**
     * Test case 4 : Internal Server Error (UPC Code)
     */
    @Test
    void returns500_for_upcCode() {
        String requestBody = "{\"upc\": \"051500255109\"}"; 

        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/products/classify")
                .then()
                .statusCode(500);
    }
    
    /**
     * Test case 5 : Internal Server Error (PLU Code)
     */
    @Test
    void returns500_for_pluCode_asString() {
        String requestBody = "{\"plu_code\": \"4011\"}";

        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/products/classify")
                .then()
                .statusCode(500);
    }

    /**
     * Test case 6 : Authentication (Invalid Key)
     */
    @Test
    void returns401_for_invalidApiKey() {
        String requestBody = "{\"title\": \"Kroger Vitamin A & D Reduced Fat 2% Milk\"}";
        given()
                .baseUri(BASE)
                .queryParam("apiKey", "THIS_IS_A_BAD_KEY")
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/products/classify")
                .then()
                .statusCode(401);
    }

    /**
     * Test case 7 : Internal Server Error (Empty Body)
     */
    @Test
    void returns500_for_emptyBody() {
        String requestBody = "{}";
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/products/classify")
                .then()
                .statusCode(500);
    }

    /**
     * Test case 8 : Bad Request (Invalid JSON Syntax)
     */
    @Test
    void returns400_for_invalidJsonSyntax() {
        String requestBody = "{\"title\": \"milk\""; // Missing closing }
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/products/classify")
                .then()
                .statusCode(400);
    }
    
    /**
     * Test case 9 : Not Found (Invalid Endpoint)
     */
    @Test
    void returns404_for_invalidEndpoint() {
        String requestBody = "{\"title\": \"milk\"}";
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/products/classify/bad-path")
                .then()
                .statusCode(404);
    }
    
    /**
     * Test case 10 : Authentication (Missing Key)
     */
    @Test
    void returns401_for_missingApiKey() {
        String requestBody = "{\"title\": \"milk\"}";
        given()
                .baseUri(BASE)
                // No apiKey param
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/products/classify")
                .then()
                .statusCode(401);
    }
    
    /**
     * Test case 11 : Internal Server Error (Empty Title)
     */
    @Test
    void returns500_for_emptyTitle() {
        String requestBody = "{\"title\": \"\"}";
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/products/classify")
                .then()
                .statusCode(500); 
    }
    
    /**
     * Test case 12 : Internal Server Error (PLU Code as Number)
     */
    @Test
    void returns500_for_pluCode_asNumber() {
        String requestBody = "{\"plu_code\": 4011}"; // PLU code as a number
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/food/products/classify")
                .then()
                .statusCode(500);
    }
}
