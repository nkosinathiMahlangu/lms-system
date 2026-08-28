import api from './axios'

export const login = (credentials) =>
  api.post('/api/auth/login', credentials).then((r) => r.data.data)

export const forgotPassword = (email) =>
  api.post('/api/auth/forgot-password', { email }).then((r) => r.data.message)

// Backend VerifyOtpResetPasswordRequest only takes: otp + newPassword
export const verifyOtp = ({ otp, newPassword }) =>
  api.post('/api/auth/verify-otp', { otp, newPassword }).then((r) => r.data.message)
