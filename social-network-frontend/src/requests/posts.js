import axios from 'axios';
import createQuery from '@/utils/createQuery';

export default {
  getById(id, params) {
    const query = createQuery(params);
    const queryString = !query.length ? '' : `&${query.join('&')}`;
    return axios.get(`post?accountIds=${id}${queryString}&sort=time,desc&isDeleted=false`);
  },

  getFeeds(params) {
    const query = createQuery(params);
    const queryString = !query.length ? '' : `&${query.join('&')}`;
    return axios.get(`post?withFriends=true${queryString}&sort=time,desc&isDeleted=false&size=5`);
  },

  get(params) {
    const query = createQuery(params);
    const queryString = !query.length ? '' : `&${query.join('&')}`;
    return axios.get(`post?&sort=time,desc&isDeleted=false&size=3${queryString}`);
  },

  push(data, isPUT = true, id = '', query = '') {
    const url = id ? `post/${id}${query}` : `post${query}`;
    return isPUT ? axios.put(url, data) : axios.post(url, data);
  },

  delete(id) {
    return axios.delete(`post/${id}`);
  },
};
