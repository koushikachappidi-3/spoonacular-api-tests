"""Tests for POST /food/ingredients/map."""
import requests
import pytest
from spoonacular import client

BASE = client.BASE_URL


def test_returns200_and_maps_oneIngredient(api_key):
    r = client.post_json(
        "/food/ingredients/map",
        api_key=api_key,
        body={"ingredients": ["flour"], "servings": 1},
    )
    assert r.status_code == 200
    data = r.json()
    assert len(data) == 1
    assert data[0]["original"] == "flour"


def test_returns200_and_maps_multipleIngredients(api_key):
    r = client.post_json(
        "/food/ingredients/map",
        api_key=api_key,
        body={"ingredients": ["1 cup flour", "2 eggs"], "servings": 2},
    )
    assert r.status_code == 200
    data = r.json()
    assert len(data) == 2
    assert data[0]["original"] == "1 cup flour"
    assert data[1]["original"] == "2 eggs"


def test_returns200_and_emptyArray_for_emptyIngredients(api_key):
    r = client.post_json(
        "/food/ingredients/map",
        api_key=api_key,
        body={"ingredients": [], "servings": 1},
    )
    assert r.status_code == 200
    assert len(r.json()) == 0


def test_returns200_when_servingsField_isMissing(api_key):
    r = client.post_json(
        "/food/ingredients/map",
        api_key=api_key,
        body={"ingredients": ["flour"]},
    )
    assert r.status_code == 200
    assert len(r.json()) == 1


def test_returns200_when_invalidServingsFormat_isIgnored(api_key):
    r = client.post_json(
        "/food/ingredients/map",
        api_key=api_key,
        body={"ingredients": ["flour"], "servings": "two"},
    )
    assert r.status_code == 200


def test_returns401_for_invalidApiKey():
    r = requests.post(
        f"{BASE}/food/ingredients/map",
        params={"apiKey": "THIS_IS_A_BAD_KEY"},
        json={"ingredients": ["flour"], "servings": 1},
        timeout=30,
    )
    assert r.status_code == 401


def test_returns401_for_missingApiKey():
    r = requests.post(
        f"{BASE}/food/ingredients/map",
        json={"ingredients": ["flour"], "servings": 1},
        timeout=30,
    )
    assert r.status_code == 401


def test_returns401_for_emptyApiKey():
    r = requests.post(
        f"{BASE}/food/ingredients/map",
        params={"apiKey": ""},
        json={"ingredients": ["flour"], "servings": 1},
        timeout=30,
    )
    assert r.status_code == 401


def test_returns500_for_missingIngredientsField(api_key):
    r = client.post_json(
        "/food/ingredients/map", api_key=api_key, body={"servings": 1}
    )
    assert r.status_code == 500


def test_returns500_for_invalidJsonSyntax(api_key):
    r = client.post_raw(
        "/food/ingredients/map",
        api_key=api_key,
        body='{"ingredients": ["flour"], "servings": 1',  # Missing closing }
    )
    assert r.status_code == 500


def test_returns500_for_invalidIngredientsFormat(api_key):
    r = client.post_json(
        "/food/ingredients/map",
        api_key=api_key,
        body={"ingredients": "flour", "servings": 1},
    )
    assert r.status_code == 500


def test_returns404_for_invalidEndpoint(api_key):
    r = client.post_json(
        "/food/ingredients/map/bad-path",
        api_key=api_key,
        body={"ingredients": ["flour"], "servings": 1},
    )
    assert r.status_code == 404
