"""Tests for GET /recipes/findByIngredients."""
import requests
import pytest
from spoonacular import client

BASE = client.BASE_URL


def test_returns200_for_oneIngredient(api_key):
    r = client.get(
        "/recipes/findByIngredients", api_key=api_key, ingredients="apples", number=2
    )
    assert r.status_code == 200
    assert len(r.json()) == 2


def test_returns200_for_multipleIngredients(api_key):
    r = client.get(
        "/recipes/findByIngredients",
        api_key=api_key,
        ingredients="apples,+flour,+sugar",
        number=1,
    )
    assert r.status_code == 200
    assert len(r.json()) == 1


def test_returns200_with_rankingParam(api_key):
    r = client.get(
        "/recipes/findByIngredients",
        api_key=api_key,
        ingredients="apples,+flour",
        number=1,
        ranking=1,
    )
    assert r.status_code == 200
    assert len(r.json()) == 1


def test_returns200_and_validDataFields(api_key):
    r = client.get(
        "/recipes/findByIngredients", api_key=api_key, ingredients="apples", number=1
    )
    assert r.status_code == 200
    data = r.json()
    assert len(data) == 1
    assert data[0]["id"] is not None
    assert data[0]["title"]
    assert data[0]["missedIngredientCount"] is not None


def test_returns200_when_invalidRanking_isIgnored(api_key):
    r = client.get(
        "/recipes/findByIngredients", api_key=api_key, ingredients="apples", ranking=3
    )
    assert r.status_code == 200
    assert len(r.json()) > 0


def test_returns200_and_emptyList_for_missingIngredientsParam(api_key):
    r = client.get("/recipes/findByIngredients", api_key=api_key, number=2)
    assert r.status_code == 200
    assert len(r.json()) == 0


def test_returns200_when_numberZero_isIgnored(api_key):
    r = client.get(
        "/recipes/findByIngredients",
        api_key=api_key,
        ingredients="apples",
        number=0,
    )
    assert r.status_code == 200
    assert len(r.json()) > 0


def test_returns200_and_emptyList_for_nonsenseIngredient(api_key):
    r = client.get(
        "/recipes/findByIngredients",
        api_key=api_key,
        ingredients="asdfjklasdfjkl",
        number=5,
    )
    assert r.status_code == 200
    assert len(r.json()) == 0


def test_returns200_when_invalidLimitLicense_isIgnored(api_key):
    r = client.get(
        "/recipes/findByIngredients",
        api_key=api_key,
        ingredients="apples",
        limitLicense="banana",
    )
    assert r.status_code == 200


def test_returns200_when_invalidIgnorePantry_isIgnored(api_key):
    r = client.get(
        "/recipes/findByIngredients",
        api_key=api_key,
        ingredients="apples",
        ignorePantry="banana",
    )
    assert r.status_code == 200


def test_returns200_when_number_outOfRange_isIgnored(api_key):
    r = client.get(
        "/recipes/findByIngredients",
        api_key=api_key,
        ingredients="apples",
        number=101,
    )
    assert r.status_code == 200


def test_returns200_and_emptyList_for_emptyIngredientsString(api_key):
    r = client.get(
        "/recipes/findByIngredients", api_key=api_key, ingredients=""
    )
    assert r.status_code == 200
    assert len(r.json()) == 0


def test_returns200_when_negativeNumber_isIgnored(api_key):
    r = client.get(
        "/recipes/findByIngredients",
        api_key=api_key,
        ingredients="apples",
        number=-1,
    )
    assert r.status_code == 200
    assert len(r.json()) > 0


def test_returns401_for_invalidApiKey():
    r = requests.get(
        f"{BASE}/recipes/findByIngredients",
        params={"apiKey": "THIS_IS_A_BAD_KEY", "ingredients": "apples"},
        timeout=30,
    )
    assert r.status_code == 401


def test_returns401_for_missingApiKey():
    r = requests.get(
        f"{BASE}/recipes/findByIngredients",
        params={"ingredients": "apples"},
        timeout=30,
    )
    assert r.status_code == 401


def test_returns401_for_emptyApiKey():
    r = requests.get(
        f"{BASE}/recipes/findByIngredients",
        params={"apiKey": "", "ingredients": "apples"},
        timeout=30,
    )
    assert r.status_code == 401


def test_returns404_for_invalidNumberFormat_string(api_key):
    r = client.get(
        "/recipes/findByIngredients",
        api_key=api_key,
        ingredients="apples",
        number="two",
    )
    assert r.status_code == 404


def test_returns404_for_invalidNumberFormat_float(api_key):
    r = client.get(
        "/recipes/findByIngredients",
        api_key=api_key,
        ingredients="apples",
        number=1.5,
    )
    assert r.status_code == 404


def test_returns404_for_invalidRankingFormat_float(api_key):
    r = client.get(
        "/recipes/findByIngredients",
        api_key=api_key,
        ingredients="apples",
        ranking=1.5,
    )
    assert r.status_code == 404


def test_returns404_for_invalidRankingFormat_string(api_key):
    r = client.get(
        "/recipes/findByIngredients",
        api_key=api_key,
        ingredients="apples",
        ranking="apple",
    )
    assert r.status_code == 404


def test_returns404_for_invalidEndpoint(api_key):
    r = client.get(
        "/recipes/findByIngredients/bad-path",
        api_key=api_key,
        ingredients="apples",
    )
    assert r.status_code == 404
