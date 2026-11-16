import api from './api';

export const matchService = {
  async getLostItemMatches(lostItemId) {
    const res = await api.get(`/lost-items/${lostItemId}/matches`);
    return res.data?.data || res.data;
  },

  async getFoundItemMatches(foundItemId) {
    const res = await api.get(`/found-items/${foundItemId}/matches`);
    return res.data?.data || res.data;
  },

  async dismissMatch(id) {
    const res = await api.patch(`/matches/${id}/dismiss`);
    return res.data;
  },

  async recalculateMatches() {
    const res = await api.post('/matches/recalculate');
    return res.data;
  },
};
