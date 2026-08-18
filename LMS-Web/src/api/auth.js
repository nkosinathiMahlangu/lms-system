import api from './axios'

export const login = (credentials) =>
  api.post('/auth/login', credentials).then((r) => r.data.data)

export const forgotPassword = (email) =>
  api.post('/auth/forgot-password', { email }).then((r) => r.data.message)

// Backend VerifyOtpResetPasswordRequest only takes: otp + newPassword (no email)
export const verifyOtp = ({ otp, newPassword }) =>
  api.post('/auth/verify-otp', { otp, newPassword }).then((r) => r.data.message)
