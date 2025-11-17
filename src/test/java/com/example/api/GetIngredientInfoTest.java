package com.example.api;

import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class GetIngredientInfoTest {
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
    void returns200_for_validIngredientId() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .pathParam("id", 9040) // ID for "banana"
                .when()
                .get("/food/ingredients/{id}/information")
                .then()
                .statusCode(200)
                .body("id", equalTo(9040))
                .body("name", equalTo("banana"));
    }

    /**
     * Test Case 2 
     */
    @Test
    void returns200_for_anotherValidIngredientId() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .pathParam("id", 9003) 
                .when()
                .get("/food/ingredients/{id}/information")
                .then()
                .statusCode(200)
                .body("id", equalTo(9003))
                .body("name", equalTo("apple"));
    }

    /**
     * Test Case 3 : With 'amount'
     */
    @Test
    void returns200_withNutrition_for_validAmount() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("amount", 2)
                .pathParam("id", 9040)
                .when()
                .get("/food/ingredients/{id}/information")
                .then()
                .statusCode(200)
                .body("id", equalTo(9040))
                .body("nutrition", notNullValue());
    }

    /**
     * Test Case 4 : With 'amount' and 'unit' (grams)
     */
    @Test
    void returns200_withNutrition_for_amountAndUnitGrams() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("amount", 100)
                .queryParam("unit", "g") // 100 grams
                .pathParam("id", 9040)
                .when()
                .get("/food/ingredients/{id}/information")
                .then()
                .statusCode(200)
                .body("id", equalTo(9040))
                .body("nutrition.weightPerServing.amount", equalTo(100))
                .body("nutrition.weightPerServing.unit", equalTo("g"));
    }

    /**
     * Test Case 5 : With 'amount' and 'unit' (ounces)
     */
    @Test
    void returns200_withNutrition_for_amountAndUnitOunces() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("amount", 5)
                .queryParam("unit", "oz") // 5 ounces
                .pathParam("id", 9003) // apple
                .when()
                .get("/food/ingredients/{id}/information")
                .then()
                .statusCode(200)
                .body("id", equalTo(9003))
                .body("nutrition", notNullValue());
    }

    /**
     * Test Case  6 : With decimal 'amount'
     */
    @Test
    void returns200_withNutrition_for_decimalAmount() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("amount", 1.5) 
                .pathParam("id", 9040)
                .when()
                .get("/food/ingredients/{id}/information")
                .then()
                .statusCode(200)
                .body("id", equalTo(9040))
                .body("nutrition", notNullValue());
    }

    /**
     * Test Case 7 : Data Validation (Default)
     */
    @Test
    void returns200_withNullNutrition_byDefault() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .pathParam("id", 9040)
                .when()
                .get("/food/ingredients/{id}/information")
                .then()
                .statusCode(200)
                .body("id", equalTo(9040))
                .body("nutrition", nullValue());
    }

    /**
     * Test Case 8 : Permissive API (Invalid Unit)
     */
    @Test
    void returns200_when_invalidUnit_isIgnored() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("amount", 1)
                .queryParam("unit", "bottles") // Invalid unit
                .pathParam("id", 9040)
                .when()
                .get("/food/ingredients/{id}/information")
                .then()
                .statusCode(200)
                .body("nutrition", notNullValue());
    }

    /**
     * Test Case 9 : Permissive API (Negative Amount)
     */
    @Test
    void returns200_when_negativeAmount_isIgnored() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("amount", -1) // Invalid amount
                .pathParam("id", 9040)
                .when()
                .get("/food/ingredients/{id}/information")
                .then()
                .statusCode(200)
                .body("nutrition", notNullValue());
    }
    
    /**
     * Test Case 10 : Permissive API (Unit without Amount)
     */
    @Test
    void returns200_when_unit_withoutAmount_isIgnored() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("unit", "g") // Unit param without amount
                .pathParam("id", 9040)
                .when()
                .get("/food/ingredients/{id}/information")
                .then()
                .statusCode(200)
                .body("nutrition", nullValue());
    }

    // --- Negative Test Case  ---

    /**
     * Test Case 11 : Authentication (Invalid Key)
     */
    @Test
    void returns401_for_invalidApiKey() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", "THIS_IS_A_BAD_KEY")
                .pathParam("id", 9040)
                .when()
                .get("/food/ingredients/{id}/information")
                .then()
                .statusCode(401); 
    }

    /**
     * Test Case 12 : Authentication (Missing Key)
     */
    @Test
    void returns401_for_missingApiKey() {
        given()
                .baseUri(BASE)
                // No apiKey param
                .pathParam("id", 9040)
                .when()
                .get("/food/ingredients/{id}/information")
                .then()
                .statusCode(401); 
    }

    /**
     * Test Case 13 : Not Found (Non-existent ID)
     */
    @Test
    void returns404_for_nonExistentIngredientId() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .pathParam("id", -1) 
                .when()
                .get("/food/ingredients/{id}/information")
                .then()
                .statusCode(404);
    }

    /**
     * Test Case 14 : Not Found (Invalid ID Format)
     */
    @Test
    void returns404_for_invalidIngredientIdFormat() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .pathParam("id", "apple") // Invalid ID format
                .when()
                .get("/food/ingredients/{id}/information")
                .then()
                .statusCode(404);
    }

    /**
     * Test Case 15 : Not Found (Invalid Amount Format)
     */
    @Test
    void returns404_for_invalidAmountFormat() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .queryParam("amount", "two") 
                .pathParam("id", 9040)
                .when()
                .get("/food/ingredients/{id}/information")
                .then()
                .statusCode(404);
    }

    /**
     * Test Case 16 : Not Found (Invalid Endpoint)
     */
    @Test
    void returns404_for_invalidSubEndpoint() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .pathParam("id", 9040)
                .when()
                .get("/food/ingredients/{id}/this-is-not-real")
                .then()
                .statusCode(404);
    }
    
    /**
     * Test Case 17 : Not Found (Zero ID)
     */
    @Test
    void returns404_for_zeroId() {
        given()
                .baseUri(BASE)
                .queryParam("apiKey", KEY)
                .pathParam("id", 0) // Invalid ID 0
                .when()
                .get("/food/ingredients/{id}/information")
                .then()
                .statusCode(404);
    }
}
