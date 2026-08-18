import { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { getAllLeaves, processLeave } from '../../api/admin'
import StatusBadge from '../../components/StatusBadge'
import Spinner from '../../components/Spinner'
import styles from './LeaveRequests.module.css'

function fmt(date) {
  if (!date) return '—'
  return new Date(date).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' })
}

const STATUS_OPTIONS = ['ALL', 'PENDING', 'APPROVED', 'REJECTED']

export default function LeaveRequests() {
  const [leaves,     setLeaves]     = useState([])
  const [filter,     setFilter]     = useState('ALL')
  const [search,     setSearch]     = useState('')
  const [loading,    setLoading]    = useState(true)
  const [actionId,   setActionId]   = useState(null)

  const load = (status) => {
    setLoading(true)
    getAllLeaves(status === 'ALL' ? null : status)
      .then(setLeaves)
      .catch(() => toast.error('Failed to load leave requests'))
      .finally(() => setLoading(false))
  }

  useEffect(() => { load(filter) }, [filter])

  const handleAction = async (id, approved) => {
    setActionId(id)
    try {
      await processLeave(id, approved)
      toast.success(approved ? 'Leave approved ✓' : 'Leave rejected')
      load(filter)
    } catch (err) {
      toast.error(err.response?.data?.message ?? 'Action failed')
    } finally { setActionId(null) }
  }

  // Client-side search on top of server-side status filter
  const visible = leaves.filter((l) => {
    if (!search.trim()) return true
    const q = search.toLowerCase()
    return (
      l.employeeName?.toLowerCase().includes(q) ||
      l.username?.toLowerCase().includes(q) ||
      l.leaveType?.toLowerCase().includes(q)
    )
  })

  return (
    <div className={styles.page}>
      <h1 className={styles.title}>Leave Requests</h1>

      {/* ── Filters ── */}
      <div className={styles.toolbar}>
        <div className={styles.statusTabs}>
          {STATUS_OPTIONS.map((s) => (
            <button
              key={s}
              className={`${styles.tab} ${filter === s ? styles.tabActive : ''}`}
              onClick={() => setFilter(s)}
            >
              {s.charAt(0) + s.slice(1).toLowerCase()}
            </button>
          ))}
        </div>
        <input
          className={styles.search}
          type="search"
          placeholder="Search employee or type…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {loading ? <Spinner /> : (
        <div className={styles.tableWrap}>
          <table className={styles.table}>
            <thead>
              <tr>
                <th>Employee</th>
                <th>Type</th>
                <th>Start</th>
                <th>End</th>
                <th>Days</th>
                <th>Reason</th>
                <th>Status</th>
                <th>Actioned By</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {visible.length === 0 ? (
                <tr><td colSpan={9} className={styles.emptyCell}>No records found.</td></tr>
              ) : visible.map((l) => (
                <tr key={l.id}>
                  <td>
                    <div className={styles.empName}>{l.employeeName}</div>
                    <div className={styles.empUser}>{l.username}</div>
                  </td>
                  <td>{l.leaveType}</td>
                  <td>{fmt(l.startDate)}</td>
                  <td>{fmt(l.endDate)}</td>
                  <td><strong>{l.numberOfDays}</strong></td>
                  <td className={styles.reason}>{l.reason}</td>
                  <td><StatusBadge status={l.status} /></td>
                  <td className={styles.dim}>{l.approvedBy ?? '—'}</td>
                  <td>
                    {l.status === 'PENDING' && (
                      <div className={styles.btnGroup}>
                        <button
                          className={styles.approveBtn}
                          disabled={actionId === l.id}
                          onClick={() => handleAction(l.id, true)}
                        >
                          ✓
                        </button>
                        <button
                          className={styles.rejectBtn}
                          disabled={actionId === l.id}
                          onClick={() => handleAction(l.id, false)}
                        >
                          ✕
                        </button>
                      </div>
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
