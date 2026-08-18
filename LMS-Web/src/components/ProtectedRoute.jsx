import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

/** Wraps routes that need authentication and optionally a specific role. */
export default function ProtectedRoute({ role }) {
  const { user } = useAuth()

  if (!user) return <Navigate to="/login" replace />
  if (role && user.role !== role) {
    // Wrong role — send to their correct dashboard
    return <Navigate to={user.role === 'ADMIN' ? '/admin' : '/employee'} replace />
  }

  return <Outlet />
}
