"""Shared pytest fixtures for the Spoonacular API test suite."""
import pytest
from spoonacular.client import get_api_key


@pytest.fixture(scope="session")
def api_key() -> str:
    """Return the API key; skip the entire session if it is missing."""
    try:
        return get_api_key()
    except EnvironmentError as exc:
        pytest.skip(str(exc))
