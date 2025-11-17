package com.example.api.mealplanner;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MealPlannerTest {

    static final String BASE = "https://api.spoonacular.com";
    static String KEY;
    
    private String spoonacularUsername;
    private String spoonacularHash;
    private int existingItemId; // We'll create one item to use in a negative test

    @BeforeAll
    void setup() {
        KEY = System.getenv("SPOONACULAR_API_KEY");
        Assumptions.assumeTrue(KEY != null && !KEY.isBlank(), "Set SPOONACULAR_API_KEY first");

        RestAssured.baseURI = BASE;

        // 1. Connect a user to get a hash
        String connectBody = "{\"username\": \"test-user-" + System.currentTimeMillis() + "\"}";
        Response response = given()
                .queryParam("apiKey", KEY) 
                .contentType(ContentType.JSON)
                .body(connectBody)
                .when()
                .post("/users/connect")
                .then()
                .statusCode(200)
                .extract().response();

        this.spoonacularUsername = response.jsonPath().getString("username");
        this.spoonacularHash = response.jsonPath().getString("hash");
        
        // 2. Set up Rest-Assured to use the API key for all other tests
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addQueryParam("apiKey", KEY)
                .build();
        
        // 3. Create one item to use for the 'DELETE with bad hash' test
        ShoppingListItem item = new ShoppingListItem("1 apple", "Fruit", true);
        this.existingItemId = given()
                .pathParam("username", this.spoonacularUsername)
                .queryParam("hash", this.spoonacularHash)
                .contentType(ContentType.JSON)
                .body(item)
                .when()
                .post("/mealplanner/{username}/shopping-list/items")
                .then()
                .statusCode(200)
                .extract().jsonPath().getInt("id");
    }

    /**
     *  Test Case 1
     * Checks that we can POST a new item and then DELETE it.
     */
    @Test
    void post_and_delete_item_workflow() {
        // --- : POST Request (Create) ---
        ShoppingListItem newItem = new ShoppingListItem("10 oz flour", "Baking", true);
        int itemId = given()
                .pathParam("username", this.spoonacularUsername)
                .queryParam("hash", this.spoonacularHash)
                .contentType(ContentType.JSON)
                .body(newItem)
        .when()
                .post("/mealplanner/{username}/shopping-list/items")
        .then()
                .statusCode(200)
                .body("name", equalTo("flour"))
                .body("id", notNullValue())
                .extract().jsonPath().getInt("id");

        // --- : DELETE Request (Clean up) ---
        given()
                .pathParam("username", this.spoonacularUsername)
                .pathParam("id", itemId) // Use the ID from the previous step
                .queryParam("hash", this.spoonacularHash)
        .when()
                .delete("/mealplanner/{username}/shopping-list/items/{id}")
        .then()
                .statusCode(200)
                .body("status", equalTo("success"));
    }
    
    /**
     *  Test Case 2 : Permissive API (Empty Body)
     * FIX: API creates an empty item, so we must also DELETE it.
     */
    @Test
    void post_returns200_and_validId_for_emptyBody() {
        String emptyBody = "{}";
        
        // --- POST Request (Create) ---
        int itemId = given()
                .pathParam("username", this.spoonacularUsername)
                .queryParam("hash", this.spoonacularHash)
                .contentType(ContentType.JSON)
                .body(emptyBody)
        .when()
                .post("/mealplanner/{username}/shopping-list/items")
        .then()
                .statusCode(200) // 
                .body("id", notNullValue()) // 
                .extract().jsonPath().getInt("id");

        // ---  DELETE Request (Clean up) ---
        given()
                .pathParam("username", this.spoonacularUsername)
                .pathParam("id", itemId)
                .queryParam("hash", this.spoonacularHash)
        .when()
                .delete("/mealplanner/{username}/shopping-list/items/{id}")
        .then()
                .statusCode(200)
                .body("status", equalTo("success"));
    }

    // --- Negative  Test Cases ---

    /**
     *  Test Case 3 : POST with Invalid Hash
     */
    @Test
    void post_returns401_for_invalidHash() {
        ShoppingListItem newItem = new ShoppingListItem("1 banana", "Fruit", true);
        given()
                .pathParam("username", this.spoonacularUsername)
                .queryParam("hash", "this-is-a-bad-hash") // Invalid hash
                .contentType(ContentType.JSON)
                .body(newItem)
        .when()
                .post("/mealplanner/{username}/shopping-list/items")
        .then()
                .statusCode(401);
    }
    
    /**
     *  Test Case 4 : DELETE with Invalid Hash
     */
    @Test
    void delete_returns401_for_invalidHash() {
        given()
                .pathParam("username", this.spoonacularUsername)
                .pathParam("id", this.existingItemId) // Use the item we made in setup
                .queryParam("hash", "this-is-a-bad-hash") // Invalid hash
        .when()
                .delete("/mealplanner/{username}/shopping-list/items/{id}")
        .then()
                .statusCode(401);
    }

    /**
     * Test Case 5 : DELETE Non-Existent Item
     */
    @Test
    void delete_returns404_for_nonExistentItem() {
        given()
                .pathParam("username", this.spoonacularUsername)
                .pathParam("id", -1) // Non-existent ID
                .queryParam("hash", this.spoonacularHash)
        .when()
                .delete("/mealplanner/{username}/shopping-list/items/{id}")
        .then()
                .statusCode(404);

    }
@AfterAll
    void cleanup() {
        RestAssured.requestSpecification = null; // Clear the global spec
    }
}
