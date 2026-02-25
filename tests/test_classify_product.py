"""Tests for POST /food/products/classify."""
import requests
import pytest
from spoonacular import client

BASE = client.BASE_URL


def test_returns200_and_classifies_byTitle(api_key):
    r = client.post_json(
        "/food/products/classify",
        api_key=api_key,
        body={"title": "Kroger Vitamin A & D Reduced Fat 2% Milk"},
    )
    assert r.status_code == 200
    assert r.json()["category"] == "2 percent milk"


def test_returns200_and_classifies_anotherTitle(api_key):
    r = client.post_json(
        "/food/products/classify",
        api_key=api_key,
        body={"title": "Skippy Creamy Peanut Butter"},
    )
    assert r.status_code == 200
    assert r.json()["category"] == "creamy peanut butter"


def test_returns200_and_classifies_byTitle_when_allFieldsPresent(api_key):
    r = client.post_json(
        "/food/products/classify",
        api_key=api_key,
        body={
            "title": "Skippy Creamy Peanut Butter",
            "upc": "051500255109",
            "plu_code": "4011",
        },
    )
    assert r.status_code == 200
    assert r.json()["category"] == "creamy peanut butter"


def test_returns500_for_upcCode(api_key):
    r = client.post_json(
        "/food/products/classify",
        api_key=api_key,
        body={"upc": "051500255109"},
    )
    assert r.status_code == 500


def test_returns500_for_pluCode_asString(api_key):
    r = client.post_json(
        "/food/products/classify",
        api_key=api_key,
        body={"plu_code": "4011"},
    )
    assert r.status_code == 500


def test_returns401_for_invalidApiKey():
    r = requests.post(
        f"{BASE}/food/products/classify",
        params={"apiKey": "THIS_IS_A_BAD_KEY"},
        json={"title": "Kroger Vitamin A & D Reduced Fat 2% Milk"},
        timeout=30,
    )
    assert r.status_code == 401


def test_returns500_for_emptyBody(api_key):
    r = client.post_json("/food/products/classify", api_key=api_key, body={})
    assert r.status_code == 500


def test_returns400_for_invalidJsonSyntax(api_key):
    r = client.post_raw(
        "/food/products/classify",
        api_key=api_key,
        body='{"title": "milk"',  # Missing closing }
    )
    assert r.status_code == 400


def test_returns404_for_invalidEndpoint(api_key):
    r = client.post_json(
        "/food/products/classify/bad-path",
        api_key=api_key,
        body={"title": "milk"},
    )
    assert r.status_code == 404


def test_returns401_for_missingApiKey():
    r = requests.post(
        f"{BASE}/food/products/classify",
        json={"title": "milk"},
        timeout=30,
    )
    assert r.status_code == 401


def test_returns500_for_emptyTitle(api_key):
    r = client.post_json(
        "/food/products/classify", api_key=api_key, body={"title": ""}
    )
    assert r.status_code == 500


def test_returns500_for_pluCode_asNumber(api_key):
    r = client.post_json(
        "/food/products/classify", api_key=api_key, body={"plu_code": 4011}
    )
    assert r.status_code == 500
