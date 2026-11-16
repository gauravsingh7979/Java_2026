import api from './api';

export const adminService = {
  async getDashboardStats() {
    const res = await api.get('/admin/dashboard');
    return res.data?.data || res.data;
  },

  async getAllUsers(params = {}) {
    const res = await api.get('/admin/users', { params });
    return res.data?.data || res.data;
  },

  async toggleUserStatus(id, active) {
    const res = await api.patch(`/admin/users/${id}/status`, { active });
    return res.data;
  },

  async getAllClaims(params = {}) {
    const res = await api.get('/admin/claims', { params });
    return res.data?.data || res.data;
  },

  async getLostReports() {
    const res = await api.get('/admin/reports/lost');
    return res.data?.data || res.data;
  },

  async getFoundReports() {
    const res = await api.get('/admin/reports/found');
    return res.data?.data || res.data;
  },

  async getAuditLogs(params = {}) {
    const res = await api.get('/admin/audit-logs', { params });
    return res.data?.data || res.data;
  },
};
