"use client";

import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  useCallback,
  useRef,
} from "react";
import { ApiError, AuthResponseDto, TokenPayload, UserRole } from "@/common/types/api";

const API_BASE = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api/v1";

interface AuthContextType {
  accessToken: string | null;
  userId: string | null;
  role: UserRole | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (token: string) => void;
  logout: () => Promise<void>;
  refreshToken: () => Promise<string | null>;
  fetchWithAuth: (
    url: string,
    options?: RequestInit,
    extraHeaders?: Record<string, string>
  ) => Promise<Response>;
}

const AuthContext = createContext<AuthContextType | null>(null);

function parseToken(token: string): TokenPayload | null {
  try {
    const payload = token.split(".")[1];
    return JSON.parse(atob(payload));
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const refreshPromiseRef = useRef<Promise<string | null> | null>(null);

  const tokenPayload = accessToken ? parseToken(accessToken) : null;
  const userId = tokenPayload?.sub ?? null;
  const role = tokenPayload?.roles?.[0] ?? null;
  const isAuthenticated = !!accessToken;

  const login = useCallback((token: string) => {
    setAccessToken(token);
  }, []);

  const clearAuth = useCallback(() => {
    setAccessToken(null);
  }, []);

  const refreshToken = useCallback(async (): Promise<string | null> => {
    // Deduplicate concurrent refresh calls
    if (refreshPromiseRef.current) {
      return refreshPromiseRef.current;
    }

    const promise = (async () => {
      try {
        const res = await fetch(`${API_BASE}/auth/refresh`, {
          method: "POST",
          credentials: "include",
        });
        if (!res.ok) {
          clearAuth();
          return null;
        }
        const data: AuthResponseDto = await res.json();
        setAccessToken(data.accessToken);
        return data.accessToken;
      } catch {
        clearAuth();
        return null;
      } finally {
        refreshPromiseRef.current = null;
      }
    })();

    refreshPromiseRef.current = promise;
    return promise;
  }, [clearAuth]);

  // Try to restore session on mount
  useEffect(() => {
    refreshToken().finally(() => setIsLoading(false));
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const logout = useCallback(async () => {
    try {
      await fetch(`${API_BASE}/auth/logout`, {
        method: "POST",
        credentials: "include",
        headers: accessToken
          ? { Authorization: `Bearer ${accessToken}` }
          : {},
      });
    } finally {
      clearAuth();
    }
  }, [accessToken, clearAuth]);

  const fetchWithAuth = useCallback(
    async (
      url: string,
      options: RequestInit = {},
      extraHeaders: Record<string, string> = {}
    ): Promise<Response> => {
      const headers: Record<string, string> = {
        "Content-Type": "application/json",
        ...extraHeaders,
      };

      if (accessToken) {
        headers["Authorization"] = `Bearer ${accessToken}`;
      }

      let res = await fetch(url, {
        ...options,
        credentials: "include",
        headers: { ...headers, ...(options.headers as Record<string, string> ?? {}) },
      });

      // Auto-refresh on 401
      if (res.status === 401 && accessToken) {
        const newToken = await refreshToken();
        if (newToken) {
          headers["Authorization"] = `Bearer ${newToken}`;
          res = await fetch(url, {
            ...options,
            credentials: "include",
            headers: { ...headers, ...(options.headers as Record<string, string> ?? {}) },
          });
        } else {
          clearAuth();
        }
      }

      return res;
    },
    [accessToken, refreshToken, clearAuth]
  );

  return (
    <AuthContext.Provider
      value={{
        accessToken,
        userId,
        role,
        isAuthenticated,
        isLoading,
        login,
        logout,
        refreshToken,
        fetchWithAuth,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}

export { API_BASE };
export type { ApiError };
