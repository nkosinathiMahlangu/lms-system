import api from './axios'

export const getMyLeaves   = ()        => api.get('/api/employee/leave').then((r) => r.data.data)
export const getBalances   = ()        => api.get('/api/employee/leave/balances').then((r) => r.data.data)
export const getLeaveTypes = ()        => api.get('/api/employee/leave/types').then((r) => r.data.data)
export const applyLeave    = (payload) => api.post('/api/employee/leave/apply', payload).then((r) => r.data.message)
export const cancelLeave   = (id)      => api.delete(`/api/employee/leave/${id}/cancel`).then((r) => r.data.message)
