import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    try {
      const saved = localStorage.getItem('findit_user');
      return saved ? JSON.parse(saved) : null;
    } catch {
      return null;
    }
  });
  const [token, setToken] = useState(() => localStorage.getItem('findit_token') || null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initAuth = async () => {
      if (token) {
        try {
          const profile = await authService.getMe();
          const userData = profile?.data || profile;
          setUser(userData);
          localStorage.setItem('findit_user', JSON.stringify(userData));
        } catch (err) {
          console.error('Failed to restore session:', err);
          logout();
        }
      }
      setLoading(false);
    };
    initAuth();
  }, [token]);

  const login = async (credentials) => {
    const res = await authService.login(credentials);
    const jwt = res?.token || res?.data?.token;
    const userData = res?.user || res?.data?.user;
    if (jwt) {
      localStorage.setItem('findit_token', jwt);
      setToken(jwt);
    }
    if (userData) {
      localStorage.setItem('findit_user', JSON.stringify(userData));
      setUser(userData);
    }
    return res;
  };

  const register = async (userDataInput) => {
    const res = await authService.register(userDataInput);
    const jwt = res?.token || res?.data?.token;
    const userData = res?.user || res?.data?.user;
    if (jwt) {
      localStorage.setItem('findit_token', jwt);
      setToken(jwt);
    }
    if (userData) {
      localStorage.setItem('findit_user', JSON.stringify(userData));
      setUser(userData);
    }
    return res;
  };

  const logout = () => {
    localStorage.removeItem('findit_token');
    localStorage.removeItem('findit_user');
    setToken(null);
    setUser(null);
  };

  const refreshUser = async () => {
    try {
      const profile = await authService.getMe();
      const userData = profile?.data || profile;
      setUser(userData);
      localStorage.setItem('findit_user', JSON.stringify(userData));
    } catch (err) {
      console.error('Failed to refresh user:', err);
    }
  };

  const isAuthenticated = !!token && !!user;
  const isAdmin = user?.role === 'ROLE_ADMIN' || user?.role === 'ROLE_SUPER_ADMIN';
  const isSuperAdmin = user?.role === 'ROLE_SUPER_ADMIN';

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated,
        isAdmin,
        isSuperAdmin,
        loading,
        login,
        register,
        logout,
        refreshUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
