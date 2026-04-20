import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
});

export const authService = {
  login: (username, password) =>
    apiClient.post('/auth/login', { username, password }),
  logout: () => apiClient.post('/auth/logout'),
  getCurrentUser: () => apiClient.get('/auth/me'),
};

export const requestService = {
  createRequest: (data) => apiClient.post('/requests', data),
  getMyRequests: () => apiClient.get('/requests/my-requests'),
  getAllRequests: () => apiClient.get('/requests'),
  getPendingRequests: () => apiClient.get('/requests/pending'),
  getRequestById: (id) => apiClient.get(`/requests/${id}`),
  approveRequest: (id, reason) =>
    apiClient.put(`/requests/${id}/approve?reason=${reason}`),
  rejectRequest: (id, reason) =>
    apiClient.put(`/requests/${id}/reject?reason=${reason}`),
};

export const inventoryService = {
  getAllInventory: () => apiClient.get('/inventory'),
  getInventoryById: (id) => apiClient.get(`/inventory/${id}`),
  createInventory: (data) => apiClient.post('/inventory', data),
  updateInventory: (id, data) => apiClient.put(`/inventory/${id}`, data),
  getLowStockItems: () => apiClient.get('/inventory/low-stock'),
};

export const auditLogService = {
  getAuditLogs: () => apiClient.get('/logs'),
};

export default apiClient;
