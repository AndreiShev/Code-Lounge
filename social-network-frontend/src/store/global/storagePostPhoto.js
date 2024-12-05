import storage from '@/requests/storage';

export default {
  namespaced: true,
  state: {
    fileName: null,
  },
  getters: {
    getStoragePostPhoto: (state) => state.fileName,
  },
  mutations: {
    setStoragePostPhoto: (state, value) => {
      state.fileName = value;
    },
  },
  actions: {
    async apiStoragePostPhoto({ commit }, imagePayload) {
      const data = new FormData();
      data.append('file', imagePayload.file);
      data.append('oldImage', imagePayload.oldImage)
      const response = await storage.api(data);
      commit('setStoragePostPhoto', response.data.fileName);
    },
  },
};
