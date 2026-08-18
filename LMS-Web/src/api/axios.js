import axios from 'axios'

const api = axios.create({
  baseURL: '',          // Vite proxy forwards /auth /employee /admin → :8080
  headers: { 'Content-Type': 'application/json' },
})

// Attach JWT on every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('lms_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// Normalise error so err.message always holds the backend's human-readable text
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      // Only hard-redirect when a token exists (session expired), not on the
      // login page itself (wrong password also returns 401).
      const token = localStorage.getItem('lms_token')
      if (token) {
        localStorage.removeItem('lms_token')
        localStorage.removeItem('lms_user')
        window.location.href = '/login'
      }
    }

    // Prefer the message inside the ApiResponse envelope; fall back to
    // the raw axios message so callers can always do err.message.
    const backendMessage =
      err.response?.data?.message ||
      err.response?.data?.error ||
      err.message

    err.message = backendMessage
    return Promise.reject(err)
  }
)

export default api
