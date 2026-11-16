import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('findit_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => {
    // Return Axios response object as-is so callers can access response.data
    return response;
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      const publicPaths = ['/login', '/register', '/forgot-password', '/reset-password'];
      const currentPath = window.location.pathname;
      if (!publicPaths.some((p) => currentPath.startsWith(p))) {
        localStorage.removeItem('findit_token');
        localStorage.removeItem('findit_user');
        window.location.href = '/login?expired=true';
      }
    }
    return Promise.reject(error);
  }
);

export default api;
