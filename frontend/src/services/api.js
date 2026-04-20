import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('osm_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('osm_token')
      localStorage.removeItem('osm_user')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

export const login = (credentials) => api.post('/auth/login', credentials)

export const getInventory = () => api.get('/inventory')
export const addInventoryItem = (data) => api.post('/inventory', data)
export const updateInventoryItem = (id, data) => api.put(`/inventory/${id}`, data)

export const getRequests = () => api.get('/requests')
export const createRequest = (data) => api.post('/requests', data)
export const approveRequest = (id) => api.put(`/requests/${id}/approve`)
export const rejectRequest = (id, data) => api.put(`/requests/${id}/reject`, data)

export default api
