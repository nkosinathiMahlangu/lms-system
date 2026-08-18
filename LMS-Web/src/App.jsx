import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './context/AuthContext'

import ProtectedRoute   from './components/ProtectedRoute'
import Layout           from './components/Layout'

import Login            from './pages/auth/Login'
import ForgotPassword   from './pages/auth/ForgotPassword'

import EmployeeDashboard from './pages/employee/EmployeeDashboard'
import ApplyLeave        from './pages/employee/ApplyLeave'
import LeaveHistory      from './pages/employee/LeaveHistory'

import AdminDashboard    from './pages/admin/AdminDashboard'
import LeaveRequests     from './pages/admin/LeaveRequests'
import ManageUsers       from './pages/admin/ManageUsers'
import LeaveTypes        from './pages/admin/LeaveTypes'

export default function App() {
  const { user } = useAuth()

  return (
    <Routes>
      {/* Public */}
      <Route path="/login"           element={<Login />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />

      {/* Root redirect */}
      <Route
        path="/"
        element={
          user
            ? <Navigate to={user.role === 'ADMIN' ? '/admin' : '/employee'} replace />
            : <Navigate to="/login" replace />
        }
      />

      {/* Employee routes */}
      <Route element={<ProtectedRoute role="EMPLOYEE" />}>
        <Route element={<Layout />}>
          <Route path="/employee"         element={<EmployeeDashboard />} />
          <Route path="/employee/apply"   element={<ApplyLeave />} />
          <Route path="/employee/history" element={<LeaveHistory />} />
        </Route>
      </Route>

      {/* Admin routes */}
      <Route element={<ProtectedRoute role="ADMIN" />}>
        <Route element={<Layout />}>
          <Route path="/admin"              element={<AdminDashboard />} />
          <Route path="/admin/leaves"       element={<LeaveRequests />} />
          <Route path="/admin/users"        element={<ManageUsers />} />
          <Route path="/admin/leave-types"  element={<LeaveTypes />} />
        </Route>
      </Route>

      {/* Catch-all */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
