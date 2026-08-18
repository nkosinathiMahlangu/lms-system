import { createContext, useContext, useState, useCallback } from 'react'
import { login as loginApi } from '../api/auth'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try { return JSON.parse(localStorage.getItem('lms_user')) } catch { return null }
  })

  const login = useCallback(async (credentials) => {
    const data = await loginApi(credentials)       // { token, username, role }
    localStorage.setItem('lms_token', data.token)
    localStorage.setItem('lms_user', JSON.stringify(data))
    setUser(data)
    return data
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem('lms_token')
    localStorage.removeItem('lms_user')
    setUser(null)
  }, [])

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
