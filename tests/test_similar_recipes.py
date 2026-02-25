"""Tests for GET /recipes/{id}/similar."""
import requests
import pytest
from spoonacular import client

BASE = client.BASE_URL
RECIPE_ID = 716429


def test_returns200_for_validRecipeId_default(api_key):
    r = client.get(f"/recipes/{RECIPE_ID}/similar", api_key=api_key)
    assert r.status_code == 200
    assert len(r.json()) > 0


def test_returns200_and_oneRecipe_with_numberOne(api_key):
    r = client.get(f"/recipes/{RECIPE_ID}/similar", api_key=api_key, number=1)
    assert r.status_code == 200
    assert len(r.json()) == 1


def test_returns200_and_correctCount_with_numberParam(api_key):
    r = client.get(f"/recipes/{RECIPE_ID}/similar", api_key=api_key, number=2)
    assert r.status_code == 200
    assert len(r.json()) == 2


def test_returns200_and_maxRecipes_with_numberMax(api_key):
    r = client.get(f"/recipes/{RECIPE_ID}/similar", api_key=api_key, number=100)
    assert r.status_code == 200
    assert len(r.json()) <= 100


def test_returns200_when_numberZero_isIgnored(api_key):
    r = client.get(f"/recipes/{RECIPE_ID}/similar", api_key=api_key, number=0)
    assert r.status_code == 200
    assert len(r.json()) > 0


def test_returns200_and_validDataFields(api_key):
    r = client.get(f"/recipes/{RECIPE_ID}/similar", api_key=api_key, number=1)
    assert r.status_code == 200
    data = r.json()
    assert len(data) == 1
    assert data[0]["id"] is not None
    assert data[0]["title"]


def test_returns200_when_number_outOfRange_isIgnored(api_key):
    r = client.get(f"/recipes/{RECIPE_ID}/similar", api_key=api_key, number=101)
    assert r.status_code == 200
    assert len(r.json()) > 0


def test_returns200_and_emptyList_for_nonExistentRecipeId(api_key):
    r = client.get("/recipes/-1/similar", api_key=api_key)
    assert r.status_code == 200
    assert len(r.json()) == 0


def test_returns401_for_invalidApiKey():
    r = requests.get(
        f"{BASE}/recipes/{RECIPE_ID}/similar",
        params={"apiKey": "THIS_IS_A_BAD_KEY"},
        timeout=30,
    )
    assert r.status_code == 401


def test_returns401_for_missingApiKey():
    r = requests.get(f"{BASE}/recipes/{RECIPE_ID}/similar", timeout=30)
    assert r.status_code == 401


def test_returns404_for_invalidRecipeIdFormat(api_key):
    r = client.get("/recipes/banana/similar", api_key=api_key)
    assert r.status_code == 404


def test_returns404_for_number_invalidFormat(api_key):
    r = client.get(f"/recipes/{RECIPE_ID}/similar", api_key=api_key, number="apple")
    assert r.status_code == 404


def test_returns404_for_invalidSubEndpoint(api_key):
    r = client.get(f"/recipes/{RECIPE_ID}/similar/this-is-not-real", api_key=api_key)
    assert r.status_code == 404
