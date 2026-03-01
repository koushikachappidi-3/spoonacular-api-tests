"""Tests for GET /food/ingredients/{id}/information."""
import requests
import pytest
from spoonacular import client

BASE = client.BASE_URL
BANANA_ID = 9040
APPLE_ID = 9003


def test_returns200_for_validIngredientId(api_key):
    r = client.get(f"/food/ingredients/{BANANA_ID}/information", api_key=api_key)
    assert r.status_code == 200
    data = r.json()
    assert data["id"] == BANANA_ID
    assert data["name"] == "banana"


def test_returns200_for_anotherValidIngredientId(api_key):
    r = client.get(f"/food/ingredients/{APPLE_ID}/information", api_key=api_key)
    assert r.status_code == 200
    data = r.json()
    assert data["id"] == APPLE_ID
    assert data["name"] == "apple"


def test_returns200_withNutrition_for_validAmount(api_key):
    r = client.get(
        f"/food/ingredients/{BANANA_ID}/information", api_key=api_key, amount=2
    )
    assert r.status_code == 200
    data = r.json()
    assert data["id"] == BANANA_ID
    assert data.get("nutrition") is not None


def test_returns200_withNutrition_for_amountAndUnitGrams(api_key):
    r = client.get(
        f"/food/ingredients/{BANANA_ID}/information",
        api_key=api_key,
        amount=100,
        unit="g",
    )
    assert r.status_code == 200
    data = r.json()
    assert data["id"] == BANANA_ID
    assert data["nutrition"]["weightPerServing"]["amount"] == 100
    assert data["nutrition"]["weightPerServing"]["unit"] == "g"


def test_returns200_withNutrition_for_amountAndUnitOunces(api_key):
    r = client.get(
        f"/food/ingredients/{APPLE_ID}/information",
        api_key=api_key,
        amount=5,
        unit="oz",
    )
    assert r.status_code == 200
    data = r.json()
    assert data["id"] == APPLE_ID
    assert data.get("nutrition") is not None


def test_returns200_withNutrition_for_decimalAmount(api_key):
    r = client.get(
        f"/food/ingredients/{BANANA_ID}/information", api_key=api_key, amount=1.5
    )
    assert r.status_code == 200
    data = r.json()
    assert data["id"] == BANANA_ID
    assert data.get("nutrition") is not None


def test_returns200_withNullNutrition_byDefault(api_key):
    r = client.get(f"/food/ingredients/{BANANA_ID}/information", api_key=api_key)
    assert r.status_code == 200
    data = r.json()
    assert data["id"] == BANANA_ID
    assert data.get("nutrition") is None


def test_returns200_when_invalidUnit_isIgnored(api_key):
    r = client.get(
        f"/food/ingredients/{BANANA_ID}/information",
        api_key=api_key,
        amount=1,
        unit="bottles",
    )
    assert r.status_code == 200
    assert r.json().get("nutrition") is not None


def test_returns200_when_negativeAmount_isIgnored(api_key):
    r = client.get(
        f"/food/ingredients/{BANANA_ID}/information", api_key=api_key, amount=-1
    )
    assert r.status_code == 200
    assert r.json().get("nutrition") is not None


def test_returns200_when_unit_withoutAmount_isIgnored(api_key):
    r = client.get(
        f"/food/ingredients/{BANANA_ID}/information", api_key=api_key, unit="g"
    )
    assert r.status_code == 200
    assert r.json().get("nutrition") is None


def test_returns401_for_invalidApiKey():
    r = requests.get(
        f"{BASE}/food/ingredients/{BANANA_ID}/information",
        params={"apiKey": "THIS_IS_A_BAD_KEY"},
        timeout=30,
    )
    assert r.status_code == 401


def test_returns401_for_missingApiKey():
    r = requests.get(
        f"{BASE}/food/ingredients/{BANANA_ID}/information", timeout=30
    )
    assert r.status_code == 401


def test_returns404_for_nonExistentIngredientId(api_key):
    r = client.get("/food/ingredients/-1/information", api_key=api_key)
    assert r.status_code == 404


def test_returns404_for_invalidIngredientIdFormat(api_key):
    r = client.get("/food/ingredients/apple/information", api_key=api_key)
    assert r.status_code == 404


def test_returns404_for_invalidAmountFormat(api_key):
    r = client.get(
        f"/food/ingredients/{BANANA_ID}/information", api_key=api_key, amount="two"
    )
    assert r.status_code == 404


def test_returns404_for_invalidSubEndpoint(api_key):
    r = client.get(f"/food/ingredients/{BANANA_ID}/this-is-not-real", api_key=api_key)
    assert r.status_code == 404


def test_returns404_for_zeroId(api_key):
    r = client.get("/food/ingredients/0/information", api_key=api_key)
    assert r.status_code == 404
