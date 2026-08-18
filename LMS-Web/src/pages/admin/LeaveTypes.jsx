import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { getAllLeaveTypes, updateLeaveType, deleteLeaveType } from '../../api/admin'
import Spinner from '../../components/Spinner'
import styles from './LeaveTypes.module.css'

export default function LeaveTypes() {
  const [types,    setTypes]    = useState([])
  const [loading,  setLoading]  = useState(true)
  const [editId,   setEditId]   = useState(null)
  const [saving,   setSaving]   = useState(false)

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
    setValue('defaultDays', t.defaultDays)
  }

  const cancelEdit = () => { setEditId(null); reset() }

  const onSave = async (data) => {
    setSaving(true)
    try {
      await updateLeaveType(editId, { name: data.name, defaultDays: Number(data.defaultDays) })
      toast.success('Leave type updated')
      setEditId(null)
      reset()
      load()
    } catch (err) {
      toast.error(err.response?.data?.message ?? 'Update failed')
    } finally { setSaving(false) }
  }

  const onDelete = async (id, name) => {
    if (!window.confirm(`Delete leave type "${name}"?`)) return
    try {
      await deleteLeaveType(id)
      toast.success('Deleted')
      load()
    } catch (err) {
      toast.error(err.response?.data?.message ?? 'Delete failed')
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
              <tr><td colSpan={3} className={styles.emptyCell}>No leave types configured.</td></tr>
            ) : types.map((t) => (
              <tr key={t.id}>
                {editId === t.id ? (
                  <>
                    <td>
                      <input className={styles.inlineInput} {...register('name', { required: true })} />
                    </td>
                    <td>
                      <input className={styles.inlineInput} style={{ width: 80 }}
                        type="number" min={1}
                        {...register('defaultDays', { required: true, min: 1 })} />
                    </td>
                    <td>
                      <div className={styles.btnGroup}>
                        <button className={styles.saveBtn} disabled={saving} onClick={handleSubmit(onSave)}>
                          {saving ? '…' : 'Save'}
                        </button>
                        <button className={styles.cancelBtn} onClick={cancelEdit}>Cancel</button>
                      </div>
                    </td>
                  </>
                ) : (
                  <>
                    <td><strong>{t.name}</strong></td>
                    <td>{t.defaultDays} days</td>
                    <td>
                      <div className={styles.btnGroup}>
                        <button className={styles.editBtn} onClick={() => startEdit(t)}>Edit</button>
                        <button className={styles.deleteBtn} onClick={() => onDelete(t.id, t.name)}>Delete</button>
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
