import api from './api';

export const notificationService = {
  async getAll(params = {}) {
    const res = await api.get('/notifications', { params });
    return res.data?.data || res.data;
  },

  async getUnreadCount() {
    const res = await api.get('/notifications/unread-count');
    const data = res.data?.data || res.data;
    return data?.unreadCount ?? 0;
  },

  async markAsRead(id) {
    const res = await api.patch(`/notifications/${id}/read`);
    return res.data;
  },

  async markAllAsRead() {
    const res = await api.patch('/notifications/read-all');
    return res.data;
  },
};
