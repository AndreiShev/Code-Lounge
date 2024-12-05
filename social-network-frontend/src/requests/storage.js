import axios from 'axios';

export default {
  api(data) {
    return axios.post('/storage/storageUserImage', data, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  postPhoto(data) {
    return axios.post('/storage', data, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};
