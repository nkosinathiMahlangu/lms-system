import api from './axios'

/* ── Leave requests ─────────────────────────────────── */
export const getAllLeaves  = (status) =>
  api.get('/api/admin/leave', { params: status ? { status } : {} }).then((r) => r.data.data)

export const processLeave = (leaveRequestId, approved) =>
  api.put('/api/admin/leave/action', { leaveRequestId, approved }).then((r) => r.data.message)

/* ── Leave types ────────────────────────────────────── */
export const getAllLeaveTypes = ()         => api.get('/api/admin/leave-types').then((r) => r.data.data)
export const updateLeaveType  = (id, body) => api.put(`/api/admin/leave-types/${id}`, body).then((r) => r.data.data)
export const deleteLeaveType  = (id)       => api.delete(`/api/admin/leave-types/${id}`).then((r) => r.data.message)

/* ── Users ──────────────────────────────────────────── */
export const getAllUsers = ()     => api.get('/api/admin/users').then((r) => r.data.data)
export const createUser = (body) => api.post('/api/admin/users', body).then((r) => r.data.message)
export const deleteUser = (id)   => api.delete(`/api/admin/users/${id}`).then((r) => r.data.message)
