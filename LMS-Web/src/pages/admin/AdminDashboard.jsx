import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import toast from 'react-hot-toast'
import { getAllLeaves, getAllUsers } from '../../api/admin'
import StatusBadge from '../../components/StatusBadge'
import Spinner from '../../components/Spinner'
import styles from './AdminDashboard.module.css'

function fmt(date) {
  if (!date) return '—'
  return new Date(date).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' })
}

export default function AdminDashboard() {
  const [leaves,  setLeaves]  = useState([])
  const [users,   setUsers]   = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([getAllLeaves(), getAllUsers()])
      .then(([l, u]) => { setLeaves(l); setUsers(u) })
      .catch(() => toast.error('Failed to load dashboard data'))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <Spinner />

  const pending  = leaves.filter((l) => l.status === 'PENDING').length
  const approved = leaves.filter((l) => l.status === 'APPROVED').length
  const rejected = leaves.filter((l) => l.status === 'REJECTED').length

  const recent = [...leaves].reverse().slice(0, 6)

  return (
    <div className={styles.page}>
      <h1 className={styles.title}>Admin Dashboard</h1>

      {/* ── Stat cards ── */}
      <div className={styles.statGrid}>
        <div className={`${styles.stat} ${styles.statTotal}`}>
          <p className={styles.statValue}>{leaves.length}</p>
          <p className={styles.statLabel}>Total Requests</p>
        </div>
        <div className={`${styles.stat} ${styles.statPending}`}>
          <p className={styles.statValue}>{pending}</p>
          <p className={styles.statLabel}>Pending</p>
        </div>
        <div className={`${styles.stat} ${styles.statApproved}`}>
          <p className={styles.statValue}>{approved}</p>
          <p className={styles.statLabel}>Approved</p>
        </div>
        <div className={`${styles.stat} ${styles.statRejected}`}>
          <p className={styles.statValue}>{rejected}</p>
          <p className={styles.statLabel}>Rejected</p>
        </div>
        <div className={`${styles.stat} ${styles.statUsers}`}>
          <p className={styles.statValue}>{users.length}</p>
          <p className={styles.statLabel}>Employees</p>
        </div>
      </div>

      {/* ── Recent leave requests ── */}
      <section style={{ marginTop: 32 }}>
        <div className={styles.sectionHeader}>
          <h2 className={styles.sectionTitle}>Recent Leave Requests</h2>
          <Link to="/admin/leaves" className={styles.viewAll}>View all →</Link>
        </div>
        <div className={styles.tableWrap}>
          <table className={styles.table}>
            <thead>
              <tr>
                <th>Employee</th>
                <th>Type</th>
                <th>Start</th>
                <th>End</th>
                <th>Days</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {recent.length === 0 ? (
                <tr><td colSpan={6} className={styles.emptyCell}>No requests yet.</td></tr>
              ) : recent.map((l) => (
                <tr key={l.id}>
                  <td>
                    <div className={styles.empName}>{l.employeeName}</div>
                    <div className={styles.empUser}>{l.username}</div>
                  </td>
                  <td>{l.leaveType}</td>
                  <td>{fmt(l.startDate)}</td>
                  <td>{fmt(l.endDate)}</td>
                  <td><strong>{l.numberOfDays}</strong></td>
                  <td><StatusBadge status={l.status} /></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  )
}
