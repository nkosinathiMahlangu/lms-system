import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { useNavigate, Link } from 'react-router-dom'
import toast from 'react-hot-toast'
import { useAuth } from '../../context/AuthContext'
import styles from './Login.module.css'

export default function Login() {
  const { login }  = useAuth()
  const navigate   = useNavigate()
  const [loading,  setLoading]  = useState(false)
  const [apiError, setApiError] = useState('')
  const [showPw,   setShowPw]   = useState(false)

  const { register, handleSubmit, formState: { errors } } = useForm()

  const onSubmit = async (data) => {
    setLoading(true)
    setApiError('')
    try {
      const user = await login(data)
      toast.success(`Welcome back, ${user.username}!`)
      navigate(user.role === 'ADMIN' ? '/admin' : '/employee', { replace: true })
    } catch (err) {
      // err.message is set by axios interceptor to backend's ApiResponse.message
      setApiError(err.message || 'Invalid username or password')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className={styles.page}>
      <div className={styles.card}>
        {/* Brand */}
        <div className={styles.brand}>
          <div className={styles.brandIcon}>L</div>
          <h1 className={styles.brandName}>LMS</h1>
        </div>
        <p className={styles.subtitle}>Leave Management System</p>

        {/* API-level error banner */}
        {apiError && (
          <div className={styles.errorBanner} role="alert">
            {apiError}
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className={styles.field}>
            <label htmlFor="username">Username</label>
            <input
              id="username"
              type="text"
              placeholder="Enter your username"
              autoComplete="username"
              {...register('username', { required: 'Username is required' })}
            />
            {errors.username && <span className={styles.err}>{errors.username.message}</span>}
          </div>

          <div className={styles.field}>
            <label htmlFor="password">Password</label>
            <div className={styles.pwWrap}>
              <input
                id="password"
                type={showPw ? 'text' : 'password'}
                placeholder="Enter your password"
                autoComplete="current-password"
                {...register('password', { required: 'Password is required' })}
              />
              <button
                type="button"
                className={styles.eyeBtn}
                onClick={() => setShowPw((v) => !v)}
                aria-label={showPw ? 'Hide password' : 'Show password'}
              >
                {showPw ? '🙈' : '👁️'}
              </button>
            </div>
            {errors.password && <span className={styles.err}>{errors.password.message}</span>}
          </div>

          <div className={styles.forgotRow}>
            <Link to="/forgot-password">Forgot password?</Link>
          </div>

          <button className={styles.btn} type="submit" disabled={loading}>
            {loading ? 'Signing in…' : 'Sign In'}
          </button>
        </form>
      </div>
    </div>
  )
}
