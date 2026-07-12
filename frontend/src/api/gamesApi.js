const API_BASE_URL = "http://localhost:8080/api";

async function handleResponse(response) {
  if (!response.ok) {
    const error = await response.json().catch(() => null);
    throw new Error(error?.message || `Zahtev nije uspeo (status ${response.status})`);
  }
  if (response.status === 204) {
    return null;
  }
  return response.json();
}

export function listGames() {
  return fetch(`${API_BASE_URL}/games`).then(handleResponse);
}

export function getGameDetails(gameId) {
  return fetch(`${API_BASE_URL}/games/${gameId}`).then(handleResponse);
}

export function getGameEvents(gameId, afterSequenceNumber) {
  const url = afterSequenceNumber != null
    ? `${API_BASE_URL}/games/${gameId}/events?afterSequenceNumber=${afterSequenceNumber}`
    : `${API_BASE_URL}/games/${gameId}/events`;
  return fetch(url).then(handleResponse);
}

export function getGameStats(gameId) {
  return fetch(`${API_BASE_URL}/games/${gameId}/stats`).then(handleResponse);
}

export function getTopScorers() {
  return fetch(`${API_BASE_URL}/players/top-scorers`).then(handleResponse);
}

export function simulateGame(gameId) {
  return fetch(`${API_BASE_URL}/games/${gameId}/simulate`, { method: "POST" }).then(handleResponse);
}
