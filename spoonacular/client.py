"""Spoonacular API client helper."""
import os

import requests
from dotenv import load_dotenv

load_dotenv()

BASE_URL = "https://api.spoonacular.com"


def get_api_key() -> str:
    """Return the Spoonacular API key from the environment."""
    key = os.environ.get("SPOONACULAR_API_KEY", "")
    if not key:
        raise EnvironmentError(
            "SPOONACULAR_API_KEY is not set. "
            "Copy .env.example to .env and add your key, or export the variable."
        )
    return key


def get(path: str, *, api_key: str, **params) -> requests.Response:
    """Send a GET request to the Spoonacular API."""
    return requests.get(
        f"{BASE_URL}{path}",
        params={"apiKey": api_key, **params},
        timeout=30,
    )


def post_json(path: str, *, api_key: str, body: dict, **params) -> requests.Response:
    """Send a POST request with a JSON body."""
    return requests.post(
        f"{BASE_URL}{path}",
        params={"apiKey": api_key, **params},
        json=body,
        timeout=30,
    )


def post_form(path: str, *, api_key: str, data: dict, **params) -> requests.Response:
    """Send a POST request with form-encoded data."""
    return requests.post(
        f"{BASE_URL}{path}",
        params={"apiKey": api_key, **params},
        data=data,
        timeout=30,
    )


def post_raw(
    path: str, *, api_key: str, body: str, content_type: str = "application/json", **params
) -> requests.Response:
    """Send a POST request with a raw string body."""
    return requests.post(
        f"{BASE_URL}{path}",
        params={"apiKey": api_key, **params},
        data=body.encode(),
        headers={"Content-Type": content_type},
        timeout=30,
    )
