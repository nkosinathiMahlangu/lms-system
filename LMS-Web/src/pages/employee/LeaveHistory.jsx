import { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { getMyLeaves, cancelLeave } from '../../api/employee'
import StatusBadge from '../../components/StatusBadge'
import Spinner from '../../components/Spinner'
import styles from './LeaveHistory.module.css'

function fmt(date) {
  if (!date) return '—'
  return new Date(date).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' })
}

export default function LeaveHistory() {
  const [leaves,  setLeaves]  = useState([])
  const [loading, setLoading] = useState(true)

  const load = () => {
    setLoading(true)
    getMyLeaves()
      .then(setLeaves)
      .catch(() => toast.error('Failed to load leave history'))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const handleCancel = async (id) => {
    if (!window.confirm('Cancel this leave request?')) return
    try {
      await cancelLeave(id)
      toast.success('Leave request cancelled')
      load()
    } catch (err) {
      toast.error(err.response?.data?.message ?? 'Failed to cancel')
    }
  }

  if (loading) return <Spinner />

  return (
    <div className={styles.page}>
      <h1 className={styles.title}>My Leave Requests</h1>

      {leaves.length === 0 ? (
        <p className={styles.empty}>You haven't submitted any leave requests yet.</p>
      ) : (
        <div className={styles.tableWrap}>
          <table className={styles.table}>
            <thead>
              <tr>
                <th>#</th>
                <th>Type</th>
                <th>Start</th>
                <th>End</th>
                <th>Days</th>
                <th>Reason</th>
                <th>Status</th>
                <th>Approved By</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {[...leaves].reverse().map((l, i) => (
                <tr key={l.id}>
                  <td className={styles.dim}>{i + 1}</td>
                  <td><strong>{l.leaveType}</strong></td>
                  <td>{fmt(l.startDate)}</td>
                  <td>{fmt(l.endDate)}</td>
                  <td><strong>{l.numberOfDays}</strong></td>
                  <td className={styles.reason}>{l.reason}</td>
                  <td><StatusBadge status={l.status} /></td>
                  <td className={styles.dim}>{l.approvedBy ?? '—'}</td>
                  <td>
                    {l.status === 'PENDING' && (
                      <button
                        className={styles.cancelBtn}
                        onClick={() => handleCancel(l.id)}
                      >
                        Cancel
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
