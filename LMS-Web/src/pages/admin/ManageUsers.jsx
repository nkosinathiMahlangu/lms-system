import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { getAllUsers, createUser, deleteUser } from '../../api/admin'
import Spinner from '../../components/Spinner'
import styles from './ManageUsers.module.css'

export default function ManageUsers() {
  const [users,      setUsers]   = useState([])
  const [loading,    setLoading] = useState(true)
  const [showForm,   setShowForm]= useState(false)
  const [submitting, setSub]     = useState(false)
  const [apiError,   setApiError]= useState('')

  const { register, handleSubmit, reset, formState: { errors } } = useForm()

  const load = () => {
    setLoading(true)
    getAllUsers()
      .then(setUsers)
      .catch(() => toast.error('Failed to load users'))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const onCreateUser = async (data) => {
    setSub(true)
    setApiError('')
    try {
      // Always create as EMPLOYEE — admins are provisioned separately
      await createUser({ ...data, role: 'EMPLOYEE' })
      toast.success('Employee account created')
      reset()
      setShowForm(false)
      load()
    } catch (err) {
      setApiError(err.message || 'Failed to create user')
    } finally { setSub(false) }
  }

  const onDelete = async (id, name) => {
    if (!window.confirm(`Delete user "${name}"? This cannot be undone.`)) return
    try {
      await deleteUser(id)
      toast.success('User deleted')
      load()
    } catch (err) {
      toast.error(err.message || 'Failed to delete user')
    }
  }

  if (loading) return <Spinner />

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <h1 className={styles.title}>Users</h1>
        <button
          className={styles.addBtn}
          onClick={() => { setShowForm((v) => !v); setApiError('') }}
        >
          {showForm ? '✕ Close' : '+ Add Employee'}
        </button>
      </div>

      {/* ── Create user form ── */}
      {showForm && (
        <div className={styles.formCard}>
          <h2 className={styles.formTitle}>New Employee Account</h2>

          {apiError && (
            <div className={styles.errorBanner} role="alert">{apiError}</div>
          )}

          <form onSubmit={handleSubmit(onCreateUser)} noValidate>
            <div className={styles.formGrid}>
              <div className={styles.field}>
                <label>First Name</label>
                <input
                  {...register('firstName', { required: 'Required' })}
                  placeholder="Nathi"
                />
                {errors.firstName && <span className={styles.err}>{errors.firstName.message}</span>}
              </div>

              <div className={styles.field}>
                <label>Last Name</label>
                <input
                  {...register('lastName', { required: 'Required' })}
                  placeholder="Mahlangu"
                />
                {errors.lastName && <span className={styles.err}>{errors.lastName.message}</span>}
              </div>

              <div className={styles.field}>
                <label>Username</label>
                <input
                  {...register('username', { required: 'Required' })}
                  placeholder="nathi"
                />
                {errors.username && <span className={styles.err}>{errors.username.message}</span>}
              </div>

              <div className={styles.field}>
                <label>Email</label>
                <input
                  type="email"
                  {...register('email', {
                    required: 'Required',
                    pattern: { value: /\S+@\S+\.\S+/, message: 'Enter a valid email' },
                  })}
                  placeholder="nathi@company.com"
                />
                {errors.email && <span className={styles.err}>{errors.email.message}</span>}
              </div>

              <div className={styles.field}>
                <label>Password</label>
                <input
                  type="password"
                  {...register('password', {
                    required: 'Required',
                    minLength: { value: 6, message: 'Min 6 characters' },
                  })}
                  placeholder="••••••"
                />
                {errors.password && <span className={styles.err}>{errors.password.message}</span>}
              </div>
            </div>

            <div className={styles.formActions}>
              <button
                type="button"
                className={styles.cancelBtn}
                onClick={() => { reset(); setShowForm(false); setApiError('') }}
              >
                Cancel
              </button>
              <button type="submit" className={styles.submitBtn} disabled={submitting}>
                {submitting ? 'Creating…' : 'Create Employee'}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* ── Users table ── */}
      <div className={styles.tableWrap}>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>#</th>
              <th>Name</th>
              <th>Username</th>
              <th>Email</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {users.length === 0 ? (
              <tr>
                <td colSpan={5} className={styles.emptyCell}>No users found.</td>
              </tr>
            ) : users.map((u, i) => (
              <tr key={u.id}>
                <td className={styles.dim}>{i + 1}</td>
                <td><strong>{u.firstName} {u.lastName}</strong></td>
                <td>{u.username}</td>
                <td>{u.email}</td>
                <td>
                  <button
                    className={styles.deleteBtn}
                    onClick={() => onDelete(u.id, `${u.firstName} ${u.lastName}`)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
