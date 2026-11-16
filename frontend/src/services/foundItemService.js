import api from './api';

export const foundItemService = {
  async getAll(params = {}) {
    const res = await api.get('/found-items', { params });
    return res.data?.data || res.data;
  },

  async getById(id) {
    const res = await api.get(`/found-items/${id}`);
    return res.data?.data || res.data;
  },

  async create(data, images = []) {
    if (images && images.length > 0) {
      const formData = new FormData();
      formData.append(
        'item',
        new Blob([JSON.stringify(data)], { type: 'application/json' })
      );
      for (let i = 0; i < images.length; i++) {
        formData.append('images', images[i]);
      }
      const res = await api.post('/found-items', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      return res.data?.data || res.data;
    } else {
      const res = await api.post('/found-items', data);
      return res.data?.data || res.data;
    }
  },

  async update(id, data) {
    const res = await api.put(`/found-items/${id}`, data);
    return res.data?.data || res.data;
  },

  async delete(id) {
    const res = await api.delete(`/found-items/${id}`);
    return res.data;
  },

  async uploadImages(id, files) {
    const formData = new FormData();
    for (let i = 0; i < files.length; i++) {
      formData.append('images', files[i]);
    }
    const res = await api.post(`/found-items/${id}/images`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data?.data || res.data;
  },
};
