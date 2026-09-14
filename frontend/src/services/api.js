import axios from 'axios'

const API_BASE_URL = '/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Add a request interceptor to automatically attach the token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Auth APIs
export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
}

// Session APIs
export const sessionAPI = {
  postRequest: (data) => api.post('/sessions', data),
  getOpenRequests: () => api.get('/sessions'),
  getRequestsBySkill: (skillTopic) => api.get(`/sessions/skill/${skillTopic}`),
  getStudentSessions: (studentId) => api.get(`/sessions/student/${studentId}`),
  getTutorSessions: (tutorId) => api.get(`/sessions/tutor/${tutorId}`),
  acceptRequest: (sessionId) => api.post(`/sessions/${sessionId}/accept`),
  startSession: (sessionId) => api.post(`/sessions/${sessionId}/start`),
  completeSession: (sessionId, durationMinutes) => 
    api.post(`/sessions/${sessionId}/complete`, null, { params: { durationMinutes } }),
  raiseDispute: (sessionId) => api.post(`/sessions/${sessionId}/dispute`),
}

// Wallet APIs
export const walletAPI = {
  getMyWallet: () => api.get('/wallet/me'),
  getUserWallet: (userId) => api.get(`/wallet/${userId}`),
  addPoints: (userId, amount) => api.post(`/wallet/${userId}/add-points`, null, { params: { amount } }),
  getMyTransactions: () => api.get('/wallet/me/transactions'),
  getUserTransactions: (userId) => api.get(`/wallet/${userId}/transactions`),
}

// Dispute APIs
export const disputeAPI = {
  getDisputes: () => api.get('/disputes'),
  raiseDispute: (sessionId, data) => api.post(`/disputes/${sessionId}`, data),
  resolveDispute: (disputeId, data) => api.put(`/disputes/${disputeId}/resolve`, data),
}

// Error handler
export const handleError = (error) => {
  if (error.response?.status === 401) {
    localStorage.removeItem('token')
    window.location.href = '/login'
  }
  return error.response?.data?.message || error.message
}

export default api