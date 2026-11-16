import api from './api';

export const authService = {
  async register(data) {
    const res = await api.post('/auth/register', data);
    return res.data?.data || res.data;
  },

  async login(credentials) {
    const res = await api.post('/auth/login', credentials);
    return res.data?.data || res.data;
  },

  async forgotPassword(data) {
    const res = await api.post('/auth/forgot-password', data);
    return res.data;
  },

  async resetPassword(data) {
    const res = await api.post('/auth/reset-password', data);
    return res.data;
  },

  async getMe() {
    const res = await api.get('/users/me');
    return res.data?.data || res.data;
  },

  async updateMe(data) {
    const res = await api.put('/users/me', data);
    return res.data?.data || res.data;
  },

  async getMyReports() {
    const res = await api.get('/users/me/reports');
    return res.data?.data || res.data;
  },
};
