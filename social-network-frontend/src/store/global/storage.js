import storage from '@/requests/storage';

export default {
  namespaced: true,
  state: {
    storage: null,
  },
  getters: {
    getStorage: (s) => s.storage,
  },
  mutations: {
    setStorage: (s, value) => {
      s.storage = value;
    },
  },
  actions: {
    async apiStorage({ commit }, imagePayload) {
      const formData = new FormData();
      formData.append('file', imagePayload.file);
      formData.append('oldImage', imagePayload.oldImage)
      const response = await storage.api(formData);
      commit('setStorage', response.data.fileName);
      return response;
    },
  },
};
