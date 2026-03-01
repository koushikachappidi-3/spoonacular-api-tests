"""Tests for POST /recipes/cuisine."""
import requests
import pytest
from spoonacular import client

BASE = client.BASE_URL


def test_returns200_and_classifies_italian(api_key):
    r = client.post_form(
        "/recipes/cuisine",
        api_key=api_key,
        data={"title": "Spaghetti Carbonara", "ingredientList": "pasta, eggs, pancetta, cheese"},
    )
    assert r.status_code == 200
    assert r.json()["cuisine"] == "Mediterranean"


def test_returns200_and_classifies_mexican(api_key):
    r = client.post_form(
        "/recipes/cuisine",
        api_key=api_key,
        data={"title": "Chicken Tacos", "ingredientList": "chicken, tortillas, salsa, cilantro"},
    )
    assert r.status_code == 200
    assert r.json()["cuisine"] == "Mexican"


def test_returns200_and_validConfidenceScore(api_key):
    r = client.post_form(
        "/recipes/cuisine", api_key=api_key, data={"title": "Tacos"}
    )
    assert r.status_code == 200
    data = r.json()
    assert data["cuisine"]
    assert 0.0 <= data["confidence"] <= 1.0


def test_returns200_when_title_isMissing(api_key):
    r = client.post_form(
        "/recipes/cuisine",
        api_key=api_key,
        data={"ingredientList": "chicken, tortillas"},
    )
    assert r.status_code == 200


def test_returns200_for_emptyBody(api_key):
    r = client.post_form("/recipes/cuisine", api_key=api_key, data={})
    assert r.status_code == 200


def test_returns200_when_invalidLanguage_isIgnored(api_key):
    r = client.post_form(
        "/recipes/cuisine",
        api_key=api_key,
        data={"title": "Tacos"},
        language="xx",
    )
    assert r.status_code == 200


def test_returns200_when_invalidContentType_isIgnored(api_key):
    r = requests.post(
        f"{BASE}/recipes/cuisine",
        params={"apiKey": api_key},
        json={"title": "Tacos"},
        timeout=30,
    )
    assert r.status_code == 200


def test_returns401_for_invalidApiKey():
    r = requests.post(
        f"{BASE}/recipes/cuisine",
        params={"apiKey": "THIS_IS_A_BAD_KEY"},
        data={"title": "Tacos"},
        timeout=30,
    )
    assert r.status_code == 401


def test_returns404_for_invalidEndpoint(api_key):
    r = client.post_form(
        "/recipes/cuisine/bad-path", api_key=api_key, data={"title": "Tacos"}
    )
    assert r.status_code == 404


def test_returns401_for_missingApiKey():
    r = requests.post(
        f"{BASE}/recipes/cuisine",
        data={"title": "Tacos"},
        timeout=30,
    )
    assert r.status_code == 401


def test_returns401_for_emptyApiKey():
    r = requests.post(
        f"{BASE}/recipes/cuisine",
        params={"apiKey": ""},
        data={"title": "Tacos"},
        timeout=30,
    )
    assert r.status_code == 401
