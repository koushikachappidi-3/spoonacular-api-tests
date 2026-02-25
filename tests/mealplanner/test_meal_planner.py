"""Tests for POST/DELETE /mealplanner/{username}/shopping-list/items."""
import time
import requests
import pytest
from spoonacular import client

BASE = client.BASE_URL


@pytest.fixture(scope="module")
def meal_plan_session(api_key):
    """Connect a new user and return (username, hash, existing_item_id)."""
    username = f"test-user-{int(time.time() * 1000)}"
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


def test_post_and_delete_item_workflow(api_key, meal_plan_session):
    username, h, _ = meal_plan_session

    # POST a new item
    r = requests.post(
        f"{BASE}/mealplanner/{username}/shopping-list/items",
        params={"apiKey": api_key, "hash": h},
        json={"item": "10 oz flour", "aisle": "Baking", "parse": True},
        timeout=30,
    )
    assert r.status_code == 200
    data = r.json()
    assert data["name"] == "flour"
    assert data["id"] is not None
    item_id = data["id"]

    # DELETE the item
    d = requests.delete(
        f"{BASE}/mealplanner/{username}/shopping-list/items/{item_id}",
        params={"apiKey": api_key, "hash": h},
        timeout=30,
    )
    assert d.status_code == 200
    assert d.json()["status"] == "success"


def test_post_returns200_and_validId_for_emptyBody(api_key, meal_plan_session):
    username, h, _ = meal_plan_session

    r = requests.post(
        f"{BASE}/mealplanner/{username}/shopping-list/items",
        params={"apiKey": api_key, "hash": h},
        json={},
        timeout=30,
    )
    assert r.status_code == 200
    item_id = r.json()["id"]
    assert item_id is not None

    # Clean up
    requests.delete(
        f"{BASE}/mealplanner/{username}/shopping-list/items/{item_id}",
        params={"apiKey": api_key, "hash": h},
        timeout=30,
    )


def test_post_returns401_for_invalidHash(api_key, meal_plan_session):
    username, _, _ = meal_plan_session

    r = requests.post(
        f"{BASE}/mealplanner/{username}/shopping-list/items",
        params={"apiKey": api_key, "hash": "this-is-a-bad-hash"},
        json={"item": "1 banana", "aisle": "Fruit", "parse": True},
        timeout=30,
    )
    assert r.status_code == 401


def test_delete_returns401_for_invalidHash(api_key, meal_plan_session):
    username, _, existing_item_id = meal_plan_session

    r = requests.delete(
        f"{BASE}/mealplanner/{username}/shopping-list/items/{existing_item_id}",
        params={"apiKey": api_key, "hash": "this-is-a-bad-hash"},
        timeout=30,
    )
    assert r.status_code == 401


def test_delete_returns404_for_nonExistentItem(api_key, meal_plan_session):
    username, h, _ = meal_plan_session

    r = requests.delete(
        f"{BASE}/mealplanner/{username}/shopping-list/items/-1",
        params={"apiKey": api_key, "hash": h},
        timeout=30,
    )
    assert r.status_code == 404
