import { apiPost, clearAuthToken, storeAuthToken } from "../../../lib/apiClient";

export interface AuthUser {
  id: number;
  email: string;
  displayName: string;
}

interface AuthResponse extends AuthUser {
  token: string;
}

export async function login(email: string, password: string): Promise<AuthUser> {
  return authenticate("/auth/login", { email, password });
}

export async function register(
  email: string,
  password: string,
  displayName: string
): Promise<AuthUser> {
  return authenticate("/auth/register", { email, password, displayName });
}

async function authenticate(
  path: string,
  body: Record<string, string>
): Promise<AuthUser> {
  const response = await apiPost<AuthResponse>(path, body);
  storeAuthToken(response.token);
  return {
    id: response.id,
    email: response.email,
    displayName: response.displayName,
  };
}

export async function logout(): Promise<void> {
  try {
    await apiPost<void>("/auth/logout", {});
  } finally {
    clearAuthToken();
  }
}
