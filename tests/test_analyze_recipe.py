"""Tests for POST /recipes/analyze."""
import requests
import pytest
from spoonacular import client

BASE = client.BASE_URL


def test_returns200_for_validIngredientList(api_key):
    body = {"title": "Ingredients Test", "instructions": "1 large apple\n2 cups flour"}
    r = client.post_json("/recipes/analyze", api_key=api_key, body=body)
    assert r.status_code == 200


def test_returns200_for_validFullRecipe(api_key):
    body = {
        "title": "My Great Recipe",
        "instructions": "1. Mix 1 cup flour with 1 egg.\n2. Bake at 350.",
    }
    r = client.post_json("/recipes/analyze", api_key=api_key, body=body)
    assert r.status_code == 200


def test_returns200_and_validDataFields(api_key):
    body = {"title": "Butter Test", "instructions": "2.5 oz butter"}
    r = client.post_json("/recipes/analyze", api_key=api_key, body=body)
    assert r.status_code == 200


def test_returns200_with_languageParam(api_key):
    body = {"title": "Apfel test", "instructions": "1 Apfel"}
    r = client.post_json("/recipes/analyze", api_key=api_key, body=body, language="de")
    assert r.status_code == 200


def test_returns200_with_nutritionParam(api_key):
    body = {"title": "Test", "instructions": "1 apple"}
    r = client.post_json(
        "/recipes/analyze", api_key=api_key, body=body, includeNutrition="true"
    )
    assert r.status_code == 200
    assert r.json().get("nutrition") is not None


def test_returns200_with_tasteParam(api_key):
    body = {"title": "Test", "instructions": "1 apple"}
    r = client.post_json(
        "/recipes/analyze", api_key=api_key, body=body, includeTaste="true"
    )
    assert r.status_code == 200
    assert r.json().get("taste") is not None


def test_returns200_when_invalidLanguage_isIgnored(api_key):
    body = {"title": "Test", "instructions": "1 apple"}
    r = client.post_json("/recipes/analyze", api_key=api_key, body=body, language="xx")
    assert r.status_code == 200


def test_returns200_when_invalidNutritionParam_isIgnored(api_key):
    body = {"title": "Test", "instructions": "1 apple"}
    r = client.post_json(
        "/recipes/analyze", api_key=api_key, body=body, includeNutrition="banana"
    )
    assert r.status_code == 200


def test_returns200_when_invalidTasteParam_isIgnored(api_key):
    body = {"title": "Test", "instructions": "1 apple"}
    r = client.post_json(
        "/recipes/analyze", api_key=api_key, body=body, includeTaste="banana"
    )
    assert r.status_code == 200


def test_returns401_for_invalidApiKey():
    r = requests.post(
        f"{BASE}/recipes/analyze",
        params={"apiKey": "THIS_IS_A_BAD_KEY"},
        json={"title": "Test", "instructions": "1 large apple"},
        timeout=30,
    )
    assert r.status_code == 401


def test_returns400_for_emptyBody(api_key):
    r = client.post_json("/recipes/analyze", api_key=api_key, body={})
    assert r.status_code == 400


def test_returns500_for_invalidJsonSyntax(api_key):
    r = client.post_raw(
        "/recipes/analyze",
        api_key=api_key,
        body='{ingredientList": "1 large apple"}',
    )
    assert r.status_code == 500


def test_returns405_for_invalidEndpoint(api_key):
    r = client.post_json(
        "/recipes/analyze-THIS-DOES-NOT-EXIST", api_key=api_key, body={"title": "Test", "instructions": "1 large apple"}
    )
    assert r.status_code == 405
