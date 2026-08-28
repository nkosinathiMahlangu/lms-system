import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { getAllLeaveTypes, updateLeaveType, deleteLeaveType } from '../../api/admin'
import Spinner from '../../components/Spinner'
import styles from './LeaveTypes.module.css'

export default function LeaveTypes() {
  const [types,   setTypes]   = useState([])
  const [loading, setLoading] = useState(true)
  const [editId,  setEditId]  = useState(null)
  const [saving,  setSaving]  = useState(false)

  const { register, handleSubmit, reset, setValue, formState: { errors } } = useForm()

  const load = () => {
    setLoading(true)
    getAllLeaveTypes()
      .then(setTypes)
      .catch(() => toast.error('Failed to load leave types'))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const startEdit = (t) => {
    setEditId(t.id)
    setValue('name', t.name)
  }

  const cancelEdit = () => { setEditId(null); reset() }

  const onSave = async (data) => {
    setSaving(true)
    try {
      // Backend UpdateLeaveTypeRequest only accepts { name } — defaultDays is not editable
      await updateLeaveType(editId, { name: data.name })
      toast.success('Leave type updated')
      setEditId(null)
      reset()
      load()
    } catch (err) {
      // err.message is already normalised by the axios interceptor
      toast.error(err.message || 'Update failed')
    } finally { setSaving(false) }
  }

  const onDelete = async (id, name) => {
    if (!window.confirm(`Delete leave type "${name}"?\n\nThis will fail if employees have balances or leave history linked to it.`)) return
    try {
      await deleteLeaveType(id)
      toast.success(`"${name}" deleted`)
      load()
    } catch (err) {
      // err.message carries the exact backend error, e.g.
      // "Cannot delete 'Annual Leave' — there are pending leave requests for this type."
      toast.error(err.message || 'Delete failed', { duration: 6000 })
    }
  }

  if (loading) return <Spinner />

  return (
    <div className={styles.page}>
      <h1 className={styles.title}>Leave Types</h1>

      <div className={styles.tableWrap}>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Name</th>
              <th>Default Days / Year</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {types.length === 0 ? (
              <tr>
                <td colSpan={3} className={styles.emptyCell}>No leave types configured.</td>
              </tr>
            ) : types.map((t) => (
              <tr key={t.id}>
                {editId === t.id ? (
                  /* ── Edit row — only name is editable ── */
                  <>
                    <td>
                      <input
                        className={styles.inlineInput}
                        placeholder="Leave type name"
                        {...register('name', { required: 'Name is required' })}
                      />
                      {errors.name && (
                        <span className={styles.inlineErr}>{errors.name.message}</span>
                      )}
                    </td>
                    {/* Default days is read-only — shown but not editable */}
                    <td className={styles.dim}>{t.defaultDays} days</td>
                    <td>
                      <div className={styles.btnGroup}>
                        <button
                          className={styles.saveBtn}
                          disabled={saving}
                          onClick={handleSubmit(onSave)}
                        >
                          {saving ? '…' : 'Save'}
                        </button>
                        <button className={styles.cancelBtn} onClick={cancelEdit}>
                          Cancel
                        </button>
                      </div>
                    </td>
                  </>
                ) : (
                  /* ── Display row ── */
                  <>
                    <td><strong>{t.name}</strong></td>
                    <td>{t.defaultDays} days</td>
                    <td>
                      <div className={styles.btnGroup}>
                        <button className={styles.editBtn} onClick={() => startEdit(t)}>
                          Edit
                        </button>
                        <button className={styles.deleteBtn} onClick={() => onDelete(t.id, t.name)}>
                          Delete
                        </button>
                      </div>
                    </td>
                  </>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
