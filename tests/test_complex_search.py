"""Tests for GET /recipes/complexSearch."""
import requests
import pytest
from spoonacular import client

BASE = client.BASE_URL


def test_returns200_for_validQuery(api_key):
    r = client.get("/recipes/complexSearch", api_key=api_key, query="pasta", number=2)
    assert r.status_code == 200
    assert len(r.json()["results"]) == 2


def test_returns200_and_emptyResults_for_nonsenseQuery(api_key):
    r = client.get(
        "/recipes/complexSearch",
        api_key=api_key,
        query="asdfjklasdfjklasdfjkl",
        number=2,
    )
    assert r.status_code == 200
    assert len(r.json()["results"]) == 0


def test_returns200_for_query_withCuisine(api_key):
    r = client.get(
        "/recipes/complexSearch",
        api_key=api_key,
        query="pasta",
        cuisine="italian",
        number=1,
    )
    assert r.status_code == 200
    assert len(r.json()["results"]) == 1


def test_returns200_for_query_withMaxReadyTime(api_key):
    r = client.get(
        "/recipes/complexSearch",
        api_key=api_key,
        query="pizza",
        maxReadyTime=30,
        number=1,
    )
    assert r.status_code == 200


def test_returns200_when_invalidRankingFormat_isIgnored(api_key):
    r = client.get(
        "/recipes/complexSearch",
        api_key=api_key,
        query="pasta",
        ranking="banana",
    )
    assert r.status_code == 200


def test_returns404_for_invalidEndpoint(api_key):
    r = client.get("/recipes/THIS_ENDPOINT_DOES_NOT_EXIST", api_key=api_key)
    assert r.status_code == 404


def test_returns401_for_missingApiKey():
    r = requests.get(
        f"{BASE}/recipes/complexSearch",
        params={"query": "pasta"},
        timeout=30,
    )
    assert r.status_code == 401


def test_returns401_for_emptyApiKey():
    r = requests.get(
        f"{BASE}/recipes/complexSearch",
        params={"apiKey": "", "query": "pasta"},
        timeout=30,
    )
    assert r.status_code == 401


def test_returns404_for_invalidNumberFormat(api_key):
    r = client.get(
        "/recipes/complexSearch",
        api_key=api_key,
        query="pasta",
        number="two",
    )
    assert r.status_code == 404


def test_returns401_for_invalidApiKey():
    r = requests.get(
        f"{BASE}/recipes/complexSearch",
        params={"apiKey": "THIS_IS_A_BAD_KEY", "query": "pasta"},
        timeout=30,
    )
    assert r.status_code == 401
