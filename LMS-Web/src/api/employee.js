import api from './axios'

export const getMyLeaves  = ()        => api.get('/employee/leave').then((r) => r.data.data)
export const getBalances  = ()        => api.get('/employee/leave/balances').then((r) => r.data.data)
export const getLeaveTypes= ()        => api.get('/employee/leave/types').then((r) => r.data.data)
export const applyLeave   = (payload) => api.post('/employee/leave/apply', payload).then((r) => r.data.message)
export const cancelLeave  = (id)      => api.delete(`/employee/leave/${id}/cancel`).then((r) => r.data.message)
