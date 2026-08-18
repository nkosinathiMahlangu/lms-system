import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { Link } from 'react-router-dom'
import toast from 'react-hot-toast'
import { forgotPassword, verifyOtp } from '../../api/auth'
import styles from './Login.module.css'
import fpStyles from './ForgotPassword.module.css'

export default function ForgotPassword() {
  const [step,    setStep]    = useState('email') // 'email' | 'otp' | 'done'
  const [loading, setLoading] = useState(false)
  const [apiError,setApiError]= useState('')
  const [showNew, setShowNew] = useState(false)
  const [showCfm, setShowCfm] = useState(false)

  const emailForm = useForm()
  const otpForm   = useForm()

  const onSendOtp = async ({ email }) => {
    setLoading(true)
    setApiError('')
    try {
      await forgotPassword(email)
      toast.success('OTP sent — check your email')
      setStep('otp')
    } catch (err) {
      setApiError(err.message || 'Failed to send OTP')
    } finally { setLoading(false) }
  }

  const onResetPassword = async (data) => {
    if (data.newPassword !== data.confirmPassword) {
      otpForm.setError('confirmPassword', { message: 'Passwords do not match' })
      return
    }
    setLoading(true)
    setApiError('')
    try {
      // Backend only expects: otp + newPassword
      await verifyOtp({ otp: data.otp, newPassword: data.newPassword })
      toast.success('Password reset successfully!')
      setStep('done')
    } catch (err) {
      setApiError(err.message || 'Invalid or expired OTP')
    } finally { setLoading(false) }
  }

  return (
    <div className={styles.page}>
      <div className={styles.card}>
        <div className={styles.brand}>
          <div className={styles.brandIcon}>L</div>
          <h1 className={styles.brandName}>LMS</h1>
        </div>
        <p className={styles.subtitle}>
          {step === 'email' && 'Reset your password'}
          {step === 'otp'   && 'Enter the OTP & new password'}
          {step === 'done'  && 'Password updated!'}
        </p>

        {apiError && (
          <div className={styles.errorBanner} role="alert">{apiError}</div>
        )}

        {/* ── Step 1: enter email ── */}
        {step === 'email' && (
          <form onSubmit={emailForm.handleSubmit(onSendOtp)} noValidate>
            <div className={styles.field}>
              <label>Email address</label>
              <input
                type="email"
                placeholder="your@email.com"
                autoComplete="email"
                {...emailForm.register('email', {
                  required: 'Email is required',
                  pattern: { value: /\S+@\S+\.\S+/, message: 'Enter a valid email' },
                })}
              />
              {emailForm.formState.errors.email && (
                <span className={styles.err}>{emailForm.formState.errors.email.message}</span>
              )}
            </div>
            <button className={styles.btn} type="submit" disabled={loading}>
              {loading ? 'Sending…' : 'Send OTP'}
            </button>
          </form>
        )}

        {/* ── Step 2: OTP + new password + confirm ── */}
        {step === 'otp' && (
          <form onSubmit={otpForm.handleSubmit(onResetPassword)} noValidate>
            <div className={styles.field}>
              <label>OTP Code</label>
              <input
                type="text"
                placeholder="6-digit code from email"
                maxLength={6}
                {...otpForm.register('otp', {
                  required: 'OTP is required',
                  pattern: { value: /^\d{6}$/, message: 'OTP must be 6 digits' },
                })}
              />
              {otpForm.formState.errors.otp && (
                <span className={styles.err}>{otpForm.formState.errors.otp.message}</span>
              )}
            </div>

            <div className={styles.field}>
              <label>New Password</label>
              <div className={styles.pwWrap}>
                <input
                  type={showNew ? 'text' : 'password'}
                  placeholder="Min 6 characters"
                  autoComplete="new-password"
                  {...otpForm.register('newPassword', {
                    required: 'New password is required',
                    minLength: { value: 6, message: 'Min 6 characters' },
                  })}
                />
                <button type="button" className={styles.eyeBtn}
                  onClick={() => setShowNew((v) => !v)}
                  aria-label={showNew ? 'Hide password' : 'Show password'}>
                  {showNew ? '🙈' : '👁️'}
                </button>
              </div>
              {otpForm.formState.errors.newPassword && (
                <span className={styles.err}>{otpForm.formState.errors.newPassword.message}</span>
              )}
            </div>

            <div className={styles.field}>
              <label>Confirm Password</label>
              <div className={styles.pwWrap}>
                <input
                  type={showCfm ? 'text' : 'password'}
                  placeholder="Repeat new password"
                  autoComplete="new-password"
                  {...otpForm.register('confirmPassword', {
                    required: 'Please confirm your password',
                  })}
                />
                <button type="button" className={styles.eyeBtn}
                  onClick={() => setShowCfm((v) => !v)}
                  aria-label={showCfm ? 'Hide password' : 'Show password'}>
                  {showCfm ? '🙈' : '👁️'}
                </button>
              </div>
              {otpForm.formState.errors.confirmPassword && (
                <span className={styles.err}>{otpForm.formState.errors.confirmPassword.message}</span>
              )}
            </div>

            <button className={styles.btn} type="submit" disabled={loading}>
              {loading ? 'Resetting…' : 'Reset Password'}
            </button>
          </form>
        )}

        {/* ── Step 3: success ── */}
        {step === 'done' && (
          <div className={fpStyles.successBox}>
            <span className={fpStyles.checkIcon}>✓</span>
            <p>Your password has been updated successfully.</p>
            <Link to="/login" className={fpStyles.loginLink}>Back to Sign In</Link>
          </div>
        )}

        {step !== 'done' && (
          <div className={styles.forgotRow} style={{ marginTop: 16 }}>
            <Link to="/login">← Back to login</Link>
          </div>
        )}
      </div>
    </div>
  )
}
