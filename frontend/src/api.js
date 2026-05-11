const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
const TOKEN_STORAGE_KEY = "travel-planner.access-token";
const USER_STORAGE_KEY = "travel-planner.user";

export function readSession() {
  const token = localStorage.getItem(TOKEN_STORAGE_KEY);
  const userText = localStorage.getItem(USER_STORAGE_KEY);
  return {
    token,
    user: userText ? JSON.parse(userText) : null
  };
}

export function saveSession(loginResponse) {
  localStorage.setItem(TOKEN_STORAGE_KEY, loginResponse.accessToken);
  localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(loginResponse.user));
}

export function clearSession() {
  localStorage.removeItem(TOKEN_STORAGE_KEY);
  localStorage.removeItem(USER_STORAGE_KEY);
}

async function request(path, options = {}) {
  const { token } = readSession();
  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {})
  };

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers
  });
  const payload = await response.json().catch(() => null);

  if (!response.ok || payload?.success === false) {
    throw new Error(payload?.message || `Request failed with ${response.status}`);
  }

  return payload?.data;
}

export function registerUser(requestBody) {
  return request("/users/register", {
    method: "POST",
    body: JSON.stringify(requestBody)
  });
}

export function loginUser(requestBody) {
  return request("/users/login", {
    method: "POST",
    body: JSON.stringify(requestBody)
  });
}

export function getCurrentUser() {
  return request("/users/me");
}

export function searchDestinations(keyword) {
  const query = new URLSearchParams({ keyword });
  return request(`/destinations?${query.toString()}`);
}

export function createItinerary(requestBody) {
  return request("/itineraries", {
    method: "POST",
    body: JSON.stringify(requestBody)
  });
}

export function listItineraries() {
  return request("/itineraries");
}

export function addDestinationToItinerary(itineraryId, destinationId) {
  return request(`/itineraries/${itineraryId}/destinations`, {
    method: "POST",
    body: JSON.stringify({ destinationId })
  });
}

export function removeDestinationFromItinerary(itineraryId, destinationId) {
  return request(`/itineraries/${itineraryId}/destinations/${destinationId}`, {
    method: "DELETE"
  });
}
