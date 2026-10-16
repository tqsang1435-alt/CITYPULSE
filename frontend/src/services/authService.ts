import { apiGet, apiPost, clearToken, setToken } from "../lib/apiClient";
import { AuthResponse, LoginRequest, RegisterRequest, User } from "../types/api";

export const authService = {
  /**
   * Authenticate with email & password.
   * Stores accessToken in localStorage upon success.
   */
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const data = await apiPost<AuthResponse>("/auth/login", credentials);
    if (data.accessToken) {
      setToken(data.accessToken);
    }
    return data;
  },

  /**
   * Register a new user account.
   * Backend returns accessToken + user upon success.
   */
  async register(data: RegisterRequest): Promise<AuthResponse> {
    const response = await apiPost<AuthResponse>("/auth/register", data);
    if (response.accessToken) {
      setToken(response.accessToken);
    }
    return response;
  },

  /**
   * Retrieves profile of current authenticated user.
   */
  async getCurrentUser(): Promise<User> {
    return apiGet<User>("/auth/me");
  },

  /**
   * Signs out user.
   * Clears local token and notifies backend if possible.
   */
  async logout(): Promise<void> {
    try {
      await apiPost("/auth/logout");
    } catch {
      // Regardless of backend response/network state, clear local token
    } finally {
      clearToken();
    }
  },
};
