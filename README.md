# Spoonacular Food API – Python API Testing Project

### [Purpose](#purpose)
### [System Requirements](#system-requirements)
### [Installation Guide](#installation-guide)
### [Running Tests](#running-tests)
### [Design Decisions](#design-decisions)
### [Test Suite](#test-suite)

## Purpose

This project is a Python-based automated API testing framework built to validate and verify multiple endpoints provided by the [Spoonacular Food API](https://spoonacular.com/food-api).

Through this project, users can:
- Send real-time HTTP requests to Spoonacular endpoints using **requests**
- Validate successful responses, error handling, and edge cases using **pytest**
- Test both positive and negative scenarios
- Maintain API key security using environment variables loaded from a `.env` file

## System Requirements

- Python 3.11+
- pip

Dependencies (installed via `requirements.txt`):
1. **pytest** – test runner
2. **requests** – HTTP client
3. **python-dotenv** – loads `.env` file into the environment

## Installation Guide

**Step 1.** Clone the repository:

```bash
git clone https://github.com/<your-username>/spoonacular-api-tests.git
cd spoonacular-api-tests
```

**Step 2.** Create and activate a virtual environment:

```bash
python -m venv .venv
source .venv/bin/activate   # Windows: .venv\Scripts\activate
```

**Step 3.** Install dependencies:

```bash
pip install -r requirements.txt
```

**Step 4.** Create an account on <https://spoonacular.com/food-api> and copy your API key from **My Console → Profile → Show/Hide API Key**.

**Step 5.** Create a `.env` file in the project root based on the provided template:

```bash
cp .env.example .env
```

Edit `.env` and replace the placeholder with your real key:

```
SPOONACULAR_API_KEY=your_actual_api_key_here
```

The `.env` file is listed in `.gitignore` and will **never** be committed.

## Running Tests

Run the full test suite:

```bash
pytest
```

Run a specific test file:

```bash
pytest tests/test_complex_search.py
```

Run with verbose output:

```bash
pytest -v
```

If `SPOONACULAR_API_KEY` is not set, tests that require it will be skipped with a helpful message.

## Design Decisions

- **Tech Stack:**
  1. **Python 3.11** for writing test automation code.
  2. **pytest** as the primary testing framework.
  3. **requests** for making HTTP requests.
  4. **python-dotenv** to load the API key from a local `.env` file.

- **Project Structure:**
  - `spoonacular/client.py` – shared helper that reads the API key and wraps `GET`, `POST JSON`, `POST form`, and `POST raw` calls.
  - `tests/conftest.py` – shared `api_key` session fixture; skips all tests gracefully when the key is missing.
  - `tests/` – one test module per endpoint; mirrors the original Java test class structure.
  - `tests/mealplanner/` – tests for the shopping-list POST/DELETE workflow.

- **API Key Management:** The key is read from the `SPOONACULAR_API_KEY` environment variable. Locally it can be provided via `.env`; in CI it is injected from GitHub repository secrets.

- **Test Design:** Each test module contains both positive and negative test cases to verify correct behavior, error handling, and response structure validation.

## Test Suite

| Test Module | Spoonacular Endpoint |
|---|---|
| `tests/test_complex_search.py` | `GET /recipes/complexSearch` |
| `tests/test_recipe_info.py` | `GET /recipes/{id}/information` |
| `tests/test_similar_recipes.py` | `GET /recipes/{id}/similar` |
| `tests/test_ingredient_info.py` | `GET /food/ingredients/{id}/information` |
| `tests/test_recipes_by_ingredients.py` | `GET /recipes/findByIngredients` |
| `tests/test_analyze_recipe.py` | `POST /recipes/analyze` |
| `tests/test_classify_cuisine.py` | `POST /recipes/cuisine` |
| `tests/test_classify_product.py` | `POST /food/products/classify` |
| `tests/test_map_ingredients.py` | `POST /food/ingredients/map` |
| `tests/mealplanner/test_meal_planner.py` | `POST/DELETE /mealplanner/{username}/shopping-list/items` |
| `tests/mealplanner/test_delete_meal_planner.py` | `DELETE /mealplanner/{username}/shopping-list/items/{id}` |

## CI

A GitHub Actions workflow (`.github/workflows/tests.yml`) runs the full test suite on every push and pull request using Python 3.11. The `SPOONACULAR_API_KEY` secret must be configured in the repository settings under **Settings → Secrets and variables → Actions**.
