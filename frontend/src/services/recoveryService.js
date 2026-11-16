import api from './api';

export const recoveryService = {
  async getMyRecoveries(params = {}) {
    const res = await api.get('/recoveries/my-recoveries', { params });
    return res.data?.data || res.data;
  },

  async getById(id) {
    const res = await api.get(`/recoveries/${id}`);
    return res.data?.data || res.data;
  },

  async scheduleHandover(id, scheduleData) {
    const res = await api.post(`/recoveries/${id}/schedule`, scheduleData);
    return res.data?.data || res.data;
  },

  async confirmHandover(id, confirmationData) {
    const res = await api.post(`/recoveries/${id}/confirm`, confirmationData);
    return res.data?.data || res.data;
  },
};
