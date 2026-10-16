import React, { createContext, useContext, useEffect, useState, useCallback } from "react";
import { User } from "../types/api";
import { authService } from "../services/authService";
import { clearToken, getToken } from "../lib/apiClient";

export interface AuthContextType {
  user: User | null;
  accessToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (displayName: string, email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  restoreSession: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [accessToken, setAccessToken] = useState<string | null>(getToken());
  const [isLoading, setIsLoading] = useState<boolean>(true);

  const restoreSession = useCallback(async () => {
    const token = getToken();
    if (!token) {
      setUser(null);
      setAccessToken(null);
      setIsLoading(false);
      return;
    }

    try {
      setAccessToken(token);
      const currentUser = await authService.getCurrentUser();
      setUser(currentUser);
    } catch {
      // Token is invalid or expired
      clearToken();
      setUser(null);
      setAccessToken(null);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    restoreSession();

    // Listen for unauthorized 401 event dispatched by apiClient
    const handleUnauthorized = () => {
      setUser(null);
      setAccessToken(null);
      clearToken();
    };

    window.addEventListener("vista:unauthorized", handleUnauthorized);
    return () => {
      window.removeEventListener("vista:unauthorized", handleUnauthorized);
    };
  }, [restoreSession]);

  const login = async (email: string, password: string) => {
    const response = await authService.login({ email, password });
    setAccessToken(response.accessToken);
    setUser(response.user);
  };

  const register = async (displayName: string, email: string, password: string) => {
    const response = await authService.register({ displayName, email, password });
    setAccessToken(response.accessToken);
    setUser(response.user);
  };

  const logout = async () => {
    try {
      await authService.logout();
    } finally {
      setUser(null);
      setAccessToken(null);
      clearToken();
    }
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        accessToken,
        isAuthenticated: Boolean(user && accessToken),
        isLoading,
        login,
        register,
        logout,
        restoreSession,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export function useAuth(): AuthContextType {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
