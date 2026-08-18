import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import { useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import { applyLeave, getLeaveTypes } from '../../api/employee'
import Spinner from '../../components/Spinner'
import styles from './ApplyLeave.module.css'

export default function ApplyLeave() {
  const navigate = useNavigate()
  const [leaveTypes,    setLeaveTypes]    = useState([])
  const [typesLoading,  setTypesLoading]  = useState(true)
  const [loading,       setLoading]       = useState(false)
  const [apiError,      setApiError]      = useState('')

  const { register, handleSubmit, watch, formState: { errors } } = useForm()

  useEffect(() => {
    getLeaveTypes()
      .then(setLeaveTypes)
      .catch(() => toast.error('Could not load leave types'))
      .finally(() => setTypesLoading(false))
  }, [])

  const startDate = watch('startDate')
  const endDate   = watch('endDate')

  const dayCount = (() => {
    if (!startDate || !endDate) return null
    const diff = (new Date(endDate) - new Date(startDate)) / 86400000 + 1
    return diff > 0 ? diff : null
  })()

  const onSubmit = async (data) => {
    setLoading(true)
    setApiError('')
    try {
      await applyLeave({
        leaveTypeId: Number(data.leaveTypeId),
        startDate:   data.startDate,
        endDate:     data.endDate,
        reason:      data.reason,
      })
      toast.success('Leave request submitted!')
      navigate('/employee/history')
    } catch (err) {
      setApiError(err.message || 'Failed to submit leave request')
    } finally {
      setLoading(false)
    }
  }

  if (typesLoading) return <Spinner />

  return (
    <div className={styles.page}>
      <h1 className={styles.title}>Request Leave</h1>

      <div className={styles.card}>
        {apiError && (
          <div className={styles.errorBanner} role="alert">{apiError}</div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          {/* Leave type */}
          <div className={styles.field}>
            <label>Leave Type</label>
            <select {...register('leaveTypeId', { required: 'Please select a leave type' })}>
              <option value="">— Select type —</option>
              {leaveTypes.map((t) => (
                <option key={t.id} value={t.id}>
                  {t.name} ({t.defaultDays} days / year)
                </option>
              ))}
            </select>
            {errors.leaveTypeId && <span className={styles.err}>{errors.leaveTypeId.message}</span>}
          </div>

          {/* Date range */}
          <div className={styles.row}>
            <div className={styles.field}>
              <label>Start Date</label>
              <input
                type="date"
                {...register('startDate', { required: 'Start date is required' })}
              />
              {errors.startDate && <span className={styles.err}>{errors.startDate.message}</span>}
            </div>
            <div className={styles.field}>
              <label>End Date</label>
              <input
                type="date"
                {...register('endDate', { required: 'End date is required' })}
              />
              {errors.endDate && <span className={styles.err}>{errors.endDate.message}</span>}
            </div>
          </div>

          {/* Live day-count preview */}
          {dayCount && (
            <div className={styles.dayPill}>
              📅 <strong>{dayCount}</strong> day{dayCount !== 1 ? 's' : ''} selected
            </div>
          )}

          {/* Reason */}
          <div className={styles.field}>
            <label>Reason</label>
            <textarea
              rows={4}
              placeholder="Briefly describe your reason for leave…"
              {...register('reason', { required: 'Reason is required' })}
            />
            {errors.reason && <span className={styles.err}>{errors.reason.message}</span>}
          </div>

          <div className={styles.actions}>
            <button type="button" className={styles.cancelBtn} onClick={() => navigate(-1)}>
              Cancel
            </button>
            <button type="submit" className={styles.submitBtn} disabled={loading}>
              {loading ? 'Submitting…' : 'Submit Request'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
