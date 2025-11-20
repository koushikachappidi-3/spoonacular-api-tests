# Spoonacular Food API – Java API Testing Project

### [Purpose](#Purpose)  
### [System Requirements](#System_Requirements)  
### [Installation Guide](#Installation_Guide)  
### [Features and project walkthrough](#Features_and_project_walkthrough)  
### [Design Decisions](#Design_Decisions)  
### [Test Suite](#Test_Suite)
## Purpose:
This project is a Java-based automated API testing framework built to validate and verify multiple endpoints provided by the Spoonacular Food API.  
The main objective of this project is to ensure that the API behaves as expected under different conditions by executing structured test cases using JUnit and REST Assured.

Through this project, users can:
- Send real-time HTTP requests to Spoonacular endpoints
- Validate successful responses, error handling, and edge cases
- Test both positive and negative scenarios
- Maintain API key security using environment variables
- Run all tests using a clean and organized Maven structure

Overall, the project demonstrates how to automate REST API testing in Java while following best practices for code readability, test design, environment setup, and API key management.
## System_Requirements:
*Prerequisite: Requires Java 8 or above and Maven installed on your machine.*

1. JUnit 5  
2. REST Assured 5.x  
3. WireMock (optional, for mock testing)  
4. dotenv-java (to securely load API keys from a `.env` file)  
5. Spoonacular API Key  

To install Java and Maven:

- Download Java from the official page: https://adoptium.net  
- Download Maven from: https://maven.apache.org/download.cgi  

After installation, verify versions using:

To verify Java and Maven installation, run:

To verify Java and Maven installation, run:

```bash
java -version
mvn -version
```
## Features_and_project_walkthrough:
Step-1. Execute the automated test suite using the following Maven command from the project root:

```bash
mvn test
```
Step-2. The test suite will send real HTTP requests to the Spoonacular Food API using REST Assured.\
These requests validate different scenarios such as:

-   Successful recipe search
-   Missing or invalid parameters
-   Unauthorized access
-   Response structure validation

Step-3. The framework includes organized test classes inside `src/test/java/` where each test focuses on a specific Spoonacular endpoint.  
The below screenshot shows all test classes available in this project:


<img width="871" height="239" alt="IMG" src="https://github.com/user-attachments/assets/36aeac57-e84f-4fcf-b80a-43bf58e3d60d" />

Step-4. For scenarios where API calls should not rely on the real Spoonacular server, optional **WireMock stubs** can be used to simulate responses.\
This helps in testing edge cases such as:

-   Slow server responses
-   500 internal server errors
-   Invalid or malformed response structures

Step-5. Each test includes assertions to verify response correctness such as:

-   Status codes
-   JSON fields
-   Data types
-   Result counts
-   Error messages

Step-6. After all tests run, Maven will display a summary of passed and failed tests along with detailed logs for each.\
Reports can be found under:
```bash
/target/surefire-reports/
```
Step-7. The `GetComplexSearchTest` class validates the Spoonacular **Complex Search API**, which allows users to search recipes using keywords and filters.  
These tests verify response codes, the presence of recipe results, and correctness of key JSON fields.  
You can run this test individually using:

```bash
mvn -Dtest=com.example.api.GetComplexSearchTest test
```
<img width="1419" height="601" alt="image" src="https://github.com/user-attachments/assets/8e743f41-4ccc-446a-ba17-64bdca157818" />

Step-8. The `GetRecipeInfoTest` class focuses on retrieving **detailed recipe information** such as title, image, servings, instructions, and extended ingredients.\
These tests ensure that all fields returned by the API are correctly structured and contain valid data.\
Run this test using:
```bash
mvn -Dtest=com.example.api.GetRecipeInfoTest test
```
<img width="1452" height="602" alt="image" src="https://github.com/user-attachments/assets/da9d0a79-c012-4dc4-9b01-7beeda394d72" />

Step-9. The `GetSimilarRecipesTest` class checks the API endpoint that provides **similar recipe recommendations** based on a given recipe ID.\
This test ensures that the API returns multiple valid recommended recipes and verifies fields like IDs, titles, and similarity scores.\
Run this test using:
```bash
mvn -Dtest=com.example.api.GetSimilarRecipesTest test
```
<img width="1448" height="624" alt="image" src="https://github.com/user-attachments/assets/a1a95367-ed84-46fc-96e3-d8209354aba4" />

Step-10. The `GetIngredientInfoTest` class is responsible for testing the Spoonacular endpoint that retrieves detailed information about specific ingredients.  
These tests validate fields such as ingredient name, nutritional values, category, consistency, and other metadata returned by the API.  
You can run this test class individually using the following command:

```bash
mvn -Dtest=com.example.api.GetIngredientInfoTest test
```

<img width="1453" height="599" alt="image" src="https://github.com/user-attachments/assets/9984ee0c-b085-4a98-b854-c9ec5a2d88c9" />

Step-11. The `GetRecipesByIngredientsTest` class validates the endpoint that finds recipes based on a list of ingredients provided by the user.  
These tests verify that the API correctly returns recipes that use the given ingredients, checks the number of matches, analyzes used vs. missing ingredients, and validates essential response fields.  
You can run this test class individually using:

```bash
mvn -Dtest=com.example.api.GetRecipesByIngredientsTest test
```

<img width="1339" height="601" alt="image" src="https://github.com/user-attachments/assets/2316778b-341d-41c0-bd2a-f593a90eef27" />






## Design_Decisions:
- Tech Stack Used:
    1. **Java 8+** for writing test automation code.
    2. **JUnit 5** as the primary testing framework.
    3. **REST Assured** for making HTTP requests and validating API responses.
    4. **Maven** for dependency management and project build lifecycle.
    5. **WireMock** (optional) for mocking API endpoints during negative and edge-case testing.
    6. **dotenv-java** to securely load environment variables from a `.env` file.

- Coding standards and clean test structure were followed throughout the project. All test classes and methods are clearly named to indicate their purpose.

- API Key Management:
    1. To secure the Spoonacular API key, the project uses a `.env` file which is not pushed to version control.
    2. An `env.example` file is provided in the repository so users can easily create their own `.env` file with the correct format.

- Test Design:
    1. A modular approach is used, separating tests based on API endpoints for clarity and maintainability.
    2. Positive and negative test cases are included to verify correct behavior, error handling, and validation of response structures.
    3. For endpoints requiring multiple query parameters, dedicated test methods were added to validate handling of missing, invalid, or malformed parameters.

- Use of WireMock:
    1. WireMock is optionally included to mock API responses for scenarios where live API calls should not be used.
    2. This helps in testing difficult edge cases such as slow responses, 500 errors, or custom simulated payloads.

- Assertions and Validation:
    1. Each test includes assertions for status codes, JSON response fields, data types, result counts, and error messages.
    2. JSON parsing and validation are handled cleanly using REST Assured's built-in matchers.

- Technical documentation is provided to help users understand the project structure, required setup, and how each test class works.
## Test_Suite:
*Prerequisite: Make sure you have the correct API key in the `.env` file; otherwise many test cases will fail due to unauthorized (401) errors.*

To run the full test suite, navigate to the project's root folder and execute the following command:

```bash
mvn test
```
If you want to run a specific test class, use:
```bash
mvn -Dtest=ClassName test
```

Example:
```bash
mvn -Dtest=ComplexSearchTest test
```

After execution, Maven will generate detailed test reports which can be found under:
```bash
/target/surefire-reports/
```

These reports provide information about passed tests, failed tests, and any errors encountered during execution.






