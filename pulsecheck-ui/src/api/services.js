import axios from 'axios';

const API_BASE = "http://localhost:8080/api";
const client = axios.create({ baseURL: API_BASE });

client.interceptors.response.use(res => res, err => {
    throw new Error(err.response?.data?.message || err.message || "Network Error");
});

export const getServices = () => client.get('/services').then(res => res.data);
export const createService = (data) => client.post('/services', data).then(res => res.data);
export const deleteService = (id) => client.delete(`/services/${id}`);
export const getChecks = (id) => client.get(`/services/${id}/checks`).then(res => res.data);
export const getStats = (id) => client.get(`/services/${id}/stats`).then(res => res.data);
