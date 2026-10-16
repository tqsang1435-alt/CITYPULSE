import { ApiErrorResponse } from "../types/api";

export const TOKEN_STORAGE_KEY = "vista_access_token";

export function getToken(): string | null {
  try {
    return localStorage.getItem(TOKEN_STORAGE_KEY);
  } catch {
    return null;
  }
}

export function setToken(token: string): void {
  try {
    localStorage.setItem(TOKEN_STORAGE_KEY, token);
  } catch {
    // LocalStorage might be disabled in private mode
  }
}

export function clearToken(): void {
  try {
    localStorage.removeItem(TOKEN_STORAGE_KEY);
  } catch {
    // Ignore error
  }
}

export class ApiClientError extends Error {
  public status: number;
  public data?: ApiErrorResponse;

  constructor(message: string, status: number, data?: ApiErrorResponse) {
    super(message);
    this.name = "ApiClientError";
    this.status = status;
    this.data = data;
  }
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api/v1";

export async function apiFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
  const url = path.startsWith("http") ? path : `${API_BASE_URL}${path}`;

  const headers = new Headers(options.headers ?? {});
  if (!headers.has("Accept")) {
    headers.set("Accept", "application/json");
  }

  // If there's a body and not already set, default to application/json
  if (options.body && typeof options.body === "string" && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  // Attach JWT Bearer token if present
  const token = getToken();
  if (token && !headers.has("Authorization")) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  const response = await fetch(url, {
    ...options,
    headers,
  });

  if (response.status === 401) {
    // Clear invalid or expired session without causing infinite redirects
    clearToken();
    if (typeof window !== "undefined") {
      window.dispatchEvent(new CustomEvent("vista:unauthorized"));
    }
  }

  if (!response.ok) {
    let errorData: ApiErrorResponse | undefined;
    let errorMessage = `Yêu cầu thất bại với mã lỗi ${response.status}`;

    try {
      const parsed = await response.json();
      errorData = parsed as ApiErrorResponse;
      if (errorData.message) {
        errorMessage = errorData.message;
      }
    } catch {
      try {
        const text = await response.text();
        if (text) errorMessage = text;
      } catch {
        // Fallback default message
      }
    }

    throw new ApiClientError(errorMessage, response.status, errorData);
  }

  // For 204 No Content or empty responses
  if (response.status === 204) {
    return {} as T;
  }

  return response.json() as Promise<T>;
}

export async function apiGet<T>(path: string, options?: RequestInit): Promise<T> {
  return apiFetch<T>(path, { ...options, method: "GET" });
}

export async function apiPost<T>(path: string, body?: unknown, options?: RequestInit): Promise<T> {
  return apiFetch<T>(path, {
    ...options,
    method: "POST",
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });
}

export async function apiPut<T>(path: string, body?: unknown, options?: RequestInit): Promise<T> {
  return apiFetch<T>(path, {
    ...options,
    method: "PUT",
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });
}

export async function apiPatch<T>(path: string, body?: unknown, options?: RequestInit): Promise<T> {
  return apiFetch<T>(path, {
    ...options,
    method: "PATCH",
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });
}

export async function apiDelete<T>(path: string, options?: RequestInit): Promise<T> {
  return apiFetch<T>(path, { ...options, method: "DELETE" });
}

