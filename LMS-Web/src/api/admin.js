import api from './axios'

/* ── Leave requests ────────────────────────── */
export const getAllLeaves    = (status) =>
  api.get('/admin/leave', { params: status ? { status } : {} }).then((r) => r.data.data)

export const processLeave   = (leaveRequestId, approved) =>
  api.put('/admin/leave/action', { leaveRequestId, approved }).then((r) => r.data.message)

/* ── Leave types ───────────────────────────── */
export const getAllLeaveTypes  = ()         => api.get('/admin/leave-types').then((r) => r.data.data)
export const updateLeaveType  = (id, body) => api.put(`/admin/leave-types/${id}`, body).then((r) => r.data.data)
export const deleteLeaveType  = (id)       => api.delete(`/admin/leave-types/${id}`).then((r) => r.data.message)

/* ── Users ─────────────────────────────────── */
export const getAllUsers    = ()      => api.get('/admin/users').then((r) => r.data.data)
export const createUser    = (body)  => api.post('/admin/users', body).then((r) => r.data.message)
export const deleteUser    = (id)    => api.delete(`/admin/users/${id}`).then((r) => r.data.message)
