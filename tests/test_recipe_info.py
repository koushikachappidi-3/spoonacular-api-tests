"""Tests for GET /recipes/{id}/information."""
import requests
import pytest
from spoonacular import client

BASE = client.BASE_URL
RECIPE_ID = 716429


def test_returns200_for_validRecipeId_default(api_key):
    r = client.get(f"/recipes/{RECIPE_ID}/information", api_key=api_key)
    assert r.status_code == 200
    data = r.json()
    assert data["id"] == RECIPE_ID
    assert data.get("nutrition") is None


def test_returns200_withNutrition_when_includeNutrition_isTrue(api_key):
    r = client.get(
        f"/recipes/{RECIPE_ID}/information",
        api_key=api_key,
        includeNutrition="true",
    )
    assert r.status_code == 200
    data = r.json()
    assert data["id"] == RECIPE_ID
    assert data.get("nutrition") is not None


def test_returns200_withoutNutrition_when_includeNutrition_isFalse(api_key):
    r = client.get(
        f"/recipes/{RECIPE_ID}/information",
        api_key=api_key,
        includeNutrition="false",
    )
    assert r.status_code == 200
    data = r.json()
    assert data["id"] == RECIPE_ID
    assert data.get("nutrition") is None


def test_returns200_withWinePairing_when_addWinePairing_isTrue(api_key):
    r = client.get(
        f"/recipes/{RECIPE_ID}/information",
        api_key=api_key,
        addWinePairing="true",
    )
    assert r.status_code == 200
    assert r.json().get("winePairing") is not None


def test_returns200_withTasteData_when_addTasteData_isTrue(api_key):
    r = client.get(
        f"/recipes/{RECIPE_ID}/information",
        api_key=api_key,
        addTasteData="true",
    )
    assert r.status_code == 200
    assert r.json().get("taste") is not None


def test_returns200_when_invalidNutritionParam_isIgnored(api_key):
    r = client.get(
        f"/recipes/{RECIPE_ID}/information",
        api_key=api_key,
        includeNutrition="banana",
    )
    assert r.status_code == 200
    assert r.json().get("nutrition") is None


def test_returns401_for_invalidApiKey():
    r = requests.get(
        f"{BASE}/recipes/{RECIPE_ID}/information",
        params={"apiKey": "THIS_IS_A_BAD_KEY"},
        timeout=30,
    )
    assert r.status_code == 401


def test_returns404_for_nonExistentRecipeId(api_key):
    r = client.get("/recipes/-1/information", api_key=api_key)
    assert r.status_code == 404


def test_returns404_for_invalidRecipeIdFormat(api_key):
    r = client.get("/recipes/banana/information", api_key=api_key)
    assert r.status_code == 404


def test_returns401_for_missingApiKey():
    r = requests.get(
        f"{BASE}/recipes/{RECIPE_ID}/information",
        timeout=30,
    )
    assert r.status_code == 401


def test_returns404_for_invalidSubEndpoint(api_key):
    r = client.get(f"/recipes/{RECIPE_ID}/this-is-not-real", api_key=api_key)
    assert r.status_code == 404
