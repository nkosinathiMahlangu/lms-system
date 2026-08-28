import axios from 'axios'

// Local dev: empty baseURL → Vite proxy forwards /api to localhost:8080
// Production: VITE_API_URL points to the Railway backend (e.g. https://xxx.railway.app)
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '',
  headers: { 'Content-Type': 'application/json' },
})

// Attach JWT on every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('lms_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  (res) => res,
  (err) => {
    const status     = err.response?.status
    const requestUrl = err.config?.url ?? ''

    // 401 on anything except auth endpoints = token expired → kick to login
    if (status === 401 && !requestUrl.includes('/api/auth/')) {
      localStorage.removeItem('lms_token')
      localStorage.removeItem('lms_user')
      setTimeout(() => { window.location.href = '/login' }, 300)
    }

    // Normalise so callers always read err.message
    err.message =
      err.response?.data?.message ||
      err.response?.data?.error   ||
      err.message

    return Promise.reject(err)
  }
)

export default api
