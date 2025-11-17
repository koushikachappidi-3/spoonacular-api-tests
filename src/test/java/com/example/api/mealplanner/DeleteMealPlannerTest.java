package com.example.api.mealplanner;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeleteMealPlannerTest {

    static final String BASE = "https://api.spoonacular.com";
    static String KEY;
    
    private String spoonacularUsername;
    private String spoonacularHash;
    private int existingItemId; // An item to use for negative tests

    @BeforeAll
    void setup() {
        KEY = System.getenv("SPOONACULAR_API_KEY");
        Assumptions.assumeTrue(KEY != null && !KEY.isBlank(), "Set SPOONACULAR_API_KEY first");

        RestAssured.baseURI = BASE;

        // 1. Connect a user to get a hash
        String connectBody = "{\"username\": \"test-user-deleter-" + System.currentTimeMillis() + "\"}";
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
        
        // 2. Create one item to use for the 'DELETE with bad hash' test
        ShoppingListItem item = new ShoppingListItem("1 apple", "Fruit", true);
        this.existingItemId = given()
                .queryParam("apiKey", KEY) // Key added here
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
     * Test Case 1 Positive
     * This test creates its own item and then deletes it.
     */
    @Test
    void delete_returns200_for_validItem() {
        // --- PART 1: Create an item to delete ---
        ShoppingListItem newItem = new ShoppingListItem("1 item to delete", "Misc", true);
        int itemId = given()
                .queryParam("apiKey", KEY)
                .pathParam("username", this.spoonacularUsername)
                .queryParam("hash", this.spoonacularHash)
                .contentType(ContentType.JSON)
                .body(newItem)
        .when()
                .post("/mealplanner/{username}/shopping-list/items")
        .then()
                .statusCode(200)
                .extract().jsonPath().getInt("id");

        // DELETE the item
        given()
                .queryParam("apiKey", KEY)
                .pathParam("username", this.spoonacularUsername)
                .pathParam("id", itemId) // Use the new ID
                .queryParam("hash", this.spoonacularHash)
        .when()
                .delete("/mealplanner/{username}/shopping-list/items/{id}")
        .then()
                .statusCode(200)
                .body("status", equalTo("success"));
    }
    
    /**
     * Test case 2 Positive : Delete Twice
     * Checks that deleting an item twice results in 404 the second time.
     */
    @Test
    void delete_returns404_when_deleting_sameItem_twice() {
        // --- : Create an item to delete ---
        ShoppingListItem newItem = new ShoppingListItem("1 item to delete twice", "Misc", true);
        int itemId = given()
                .queryParam("apiKey", KEY)
                .pathParam("username", this.spoonacularUsername)
                .queryParam("hash", this.spoonacularHash)
                .contentType(ContentType.JSON)
                .body(newItem)
        .when()
                .post("/mealplanner/{username}/shopping-list/items")
        .then()
                .statusCode(200)
                .extract().jsonPath().getInt("id");

        //  DELETE the item (First time) ---
        given()
                .queryParam("apiKey", KEY)
                .pathParam("username", this.spoonacularUsername)
                .pathParam("id", itemId)
                .queryParam("hash", this.spoonacularHash)
        .when()
                .delete("/mealplanner/{username}/shopping-list/items/{id}")
        .then()
                .statusCode(200)
                .body("status", equalTo("success"));
                
        // --- DELETE the item (Second time) ---
        given()
                .queryParam("apiKey", KEY)
                .pathParam("username", this.spoonacularUsername)
                .pathParam("id", itemId) // Same ID
                .queryParam("hash", this.spoonacularHash)
        .when()
                .delete("/mealplanner/{username}/shopping-list/items/{id}")
        .then()
                .statusCode(404); // Should be Not Found
    }


    // --- Negative Test cases ---

    /**
     * Test Case 3  DELETE with Invalid Hash
     */
    @Test
    void delete_returns401_for_invalidHash() {
        given()
                .queryParam("apiKey", KEY)
                .pathParam("username", this.spoonacularUsername)
                .pathParam("id", this.existingItemId) // Use the item from setup
                .queryParam("hash", "this-is-a-bad-hash") // Invalid hash
        .when()
                .delete("/mealplanner/{username}/shopping-list/items/{id}")
        .then()
                .statusCode(401);
    }

    /**
     * Test Case 4 : DELETE Non-Existent Item
     */
    @Test
    void delete_returns404_for_nonExistentItem() {
        given()
                .queryParam("apiKey", KEY)
                .pathParam("username", this.spoonacularUsername)
                .pathParam("id", -1) // Non-existent ID
                .queryParam("hash", this.spoonacularHash)
        .when()
                .delete("/mealplanner/{username}/shopping-list/items/{id}")
        .then()
                .statusCode(404);
    }
    
    /**
     * Test Case 5 : Authentication (Missing Key)
     */
    @Test
    void delete_returns401_for_missingApiKey() {
        given()
                // No apiKey param
                .pathParam("username", this.spoonacularUsername)
                .pathParam("id", this.existingItemId)
                .queryParam("hash", this.spoonacularHash)
        .when()
                .delete("/mealplanner/{username}/shopping-list/items/{id}")
        .then()
                .statusCode(401);
    }
    
    /**
     * Test Case 6 : Authentication (Empty Key)
     */
    @Test
    void delete_returns401_for_emptyApiKey() {
        given()
                .queryParam("apiKey", "") // Empty key
                .pathParam("username", this.spoonacularUsername)
                .pathParam("id", this.existingItemId)
                .queryParam("hash", this.spoonacularHash)
        .when()
                .delete("/mealplanner/{username}/shopping-list/items/{id}")
        .then()
                .statusCode(401);
    }
    
    /**
     * Test Case 7 : Not Found (Invalid ID Format)
     */
    @Test
    void delete_returns404_for_invalidIdFormat() {
        given()
                .queryParam("apiKey", KEY)
                .pathParam("username", this.spoonacularUsername)
                .pathParam("id", "banana") // Invalid format
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
