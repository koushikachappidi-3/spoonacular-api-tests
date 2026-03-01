"""Tests for DELETE /mealplanner/{username}/shopping-list/items/{id}."""
import time
import requests
import pytest
from spoonacular import client

BASE = client.BASE_URL


@pytest.fixture(scope="module")
def delete_session(api_key):
    """Connect a dedicated user for delete tests; yield (username, hash, existing_item_id)."""
    username = f"test-user-deleter-{int(time.time() * 1000)}"
    r = requests.post(
        f"{BASE}/users/connect",
        params={"apiKey": api_key},
        json={"username": username},
        timeout=30,
    )
    assert r.status_code == 200
    data = r.json()
    spoonacular_username = data["username"]
    spoonacular_hash = data["hash"]

    # Create one item to use in negative tests
    item_r = requests.post(
        f"{BASE}/mealplanner/{spoonacular_username}/shopping-list/items",
        params={"apiKey": api_key, "hash": spoonacular_hash},
        json={"item": "1 apple", "aisle": "Fruit", "parse": True},
        timeout=30,
    )
    assert item_r.status_code == 200
    existing_item_id = item_r.json()["id"]

    yield spoonacular_username, spoonacular_hash, existing_item_id


def test_delete_returns200_for_validItem(api_key, delete_session):
    username, h, _ = delete_session

    # Create an item to delete
    r = requests.post(
        f"{BASE}/mealplanner/{username}/shopping-list/items",
        params={"apiKey": api_key, "hash": h},
        json={"item": "1 item to delete", "aisle": "Misc", "parse": True},
        timeout=30,
    )
    assert r.status_code == 200
    item_id = r.json()["id"]

    d = requests.delete(
        f"{BASE}/mealplanner/{username}/shopping-list/items/{item_id}",
        params={"apiKey": api_key, "hash": h},
        timeout=30,
    )
    assert d.status_code == 200
    assert d.json()["status"] == "success"


def test_delete_returns404_when_deleting_sameItem_twice(api_key, delete_session):
    username, h, _ = delete_session

    # Create an item
    r = requests.post(
        f"{BASE}/mealplanner/{username}/shopping-list/items",
        params={"apiKey": api_key, "hash": h},
        json={"item": "1 item to delete twice", "aisle": "Misc", "parse": True},
        timeout=30,
    )
    assert r.status_code == 200
    item_id = r.json()["id"]

    # First delete
    d1 = requests.delete(
        f"{BASE}/mealplanner/{username}/shopping-list/items/{item_id}",
        params={"apiKey": api_key, "hash": h},
        timeout=30,
    )
    assert d1.status_code == 200
    assert d1.json()["status"] == "success"

    # Second delete – should be 404
    d2 = requests.delete(
        f"{BASE}/mealplanner/{username}/shopping-list/items/{item_id}",
        params={"apiKey": api_key, "hash": h},
        timeout=30,
    )
    assert d2.status_code == 404


def test_delete_returns401_for_invalidHash(api_key, delete_session):
    username, _, existing_item_id = delete_session

    r = requests.delete(
        f"{BASE}/mealplanner/{username}/shopping-list/items/{existing_item_id}",
        params={"apiKey": api_key, "hash": "this-is-a-bad-hash"},
        timeout=30,
    )
    assert r.status_code == 401


def test_delete_returns404_for_nonExistentItem(api_key, delete_session):
    username, h, _ = delete_session

    r = requests.delete(
        f"{BASE}/mealplanner/{username}/shopping-list/items/-1",
        params={"apiKey": api_key, "hash": h},
        timeout=30,
    )
    assert r.status_code == 404


def test_delete_returns401_for_missingApiKey(delete_session):
    username, h, existing_item_id = delete_session

    r = requests.delete(
        f"{BASE}/mealplanner/{username}/shopping-list/items/{existing_item_id}",
        params={"hash": h},
        timeout=30,
    )
    assert r.status_code == 401


def test_delete_returns401_for_emptyApiKey(delete_session):
    username, h, existing_item_id = delete_session

    r = requests.delete(
        f"{BASE}/mealplanner/{username}/shopping-list/items/{existing_item_id}",
        params={"apiKey": "", "hash": h},
        timeout=30,
    )
    assert r.status_code == 401


def test_delete_returns404_for_invalidIdFormat(api_key, delete_session):
    username, h, _ = delete_session

    r = requests.delete(
        f"{BASE}/mealplanner/{username}/shopping-list/items/banana",
        params={"apiKey": api_key, "hash": h},
        timeout=30,
    )
    assert r.status_code == 404
