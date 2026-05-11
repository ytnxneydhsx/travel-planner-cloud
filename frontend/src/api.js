const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
const TOKEN_STORAGE_KEY = "travel-planner.access-token";
const USER_STORAGE_KEY = "travel-planner.user";

const ERROR_MESSAGE_MAP = {
  "Username already exists.": "用户名已存在，请换一个用户名。",
  "Username or password is invalid.": "用户名或密码不正确。",
  "User is disabled.": "该用户已被禁用。",
  "Unauthorized.": "登录状态已失效，请重新登录。",
  "User not found.": "用户不存在。",
  "Destination not found.": "目的地不存在。",
  "Destination service request failed.": "目的地服务暂时不可用，请稍后再试。",
  "Destination already exists in itinerary.": "该目的地已在当前行程中。",
  "Destination ids must be unique.": "行程中的目的地不能重复。",
  "Itinerary not found.": "行程不存在。",
  "Forbidden.": "你没有权限访问这个行程。",
  "Destination does not exist in itinerary.": "该目的地不在当前行程中。",
  "Request validation failed.": "表单校验未通过，请检查输入内容。",
  "Internal server error.": "服务器暂时不可用，请稍后再试。"
};

function toDisplayErrorMessage(message, status) {
  if (message && ERROR_MESSAGE_MAP[message]) {
    return ERROR_MESSAGE_MAP[message];
  }

  if (message && /[\u4e00-\u9fff]/.test(message)) {
    return message;
  }

  if (status) {
    return `请求失败，状态码：${status}。`;
  }

  return "请求失败，请稍后重试。";
}

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

  let response;
  try {
    response = await fetch(`${API_BASE_URL}${path}`, {
      ...options,
      headers
    });
  } catch {
    throw new Error("无法连接到网关服务，请确认后端测试环境已启动。");
  }

  const payload = await response.json().catch(() => null);

  if (!response.ok || payload?.success === false) {
    throw new Error(toDisplayErrorMessage(payload?.message, response.status));
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

export function updateItinerary(itineraryId, requestBody) {
  return request(`/itineraries/${itineraryId}`, {
    method: "PUT",
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
