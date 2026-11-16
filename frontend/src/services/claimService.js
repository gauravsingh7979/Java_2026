import api from './api';

export const claimService = {
  async submitClaim(data) {
    const res = await api.post('/claims', data);
    return res.data?.data || res.data;
  },

  async getMyClaims(params = {}) {
    const res = await api.get('/claims/my-claims', { params });
    return res.data?.data || res.data;
  },

  async getById(id) {
    const res = await api.get(`/claims/${id}`);
    return res.data?.data || res.data;
  },

  async markUnderReview(id) {
    const res = await api.patch(`/admin/claims/${id}/review`);
    return res.data?.data || res.data;
  },

  async approveClaim(id, decisionReason = 'Approved by administrator after verification') {
    const res = await api.patch(`/admin/claims/${id}/approve`, { decisionReason });
    return res.data?.data || res.data;
  },

  async rejectClaim(id, decisionReason = 'Claim rejected: verification details did not match.') {
    const res = await api.patch(`/admin/claims/${id}/reject`, { decisionReason });
    return res.data?.data || res.data;
  },
};
