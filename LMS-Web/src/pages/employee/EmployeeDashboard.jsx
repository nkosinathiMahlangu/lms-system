import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import toast from 'react-hot-toast'
import { useAuth } from '../../context/AuthContext'
import { getBalances, getMyLeaves } from '../../api/employee'
import StatusBadge from '../../components/StatusBadge'
import Spinner from '../../components/Spinner'
import styles from './EmployeeDashboard.module.css'

function fmt(date) {
  if (!date) return '—'
  return new Date(date).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' })
}

export default function EmployeeDashboard() {
  const { user } = useAuth()
  const [balances, setBalances]     = useState([])
  const [leaves,   setLeaves]       = useState([])
  const [loading,  setLoading]      = useState(true)

  useEffect(() => {
    Promise.all([getBalances(), getMyLeaves()])
      .then(([b, l]) => { setBalances(b); setLeaves(l) })
      .catch(() => toast.error('Failed to load dashboard data'))
      .finally(() => setLoading(false))
  }, [])

  // greeting by time of day
  const hour = new Date().getHours()
  const greeting =
    hour < 12 ? 'Good morning' : hour < 17 ? 'Good afternoon' : 'Good evening'

  const recent = [...leaves].reverse().slice(0, 5)

  if (loading) return <Spinner />

  return (
    <div className={styles.page}>
      {/* ── Greeting hero ── */}
      <div className={styles.hero}>
        <div>
          <p className={styles.greetSub}>{greeting},</p>
          <h1 className={styles.greetName}>{user?.username} 👋</h1>
          <p className={styles.greetHint}>Here's your leave summary for today.</p>
        </div>
        <Link to="/employee/apply" className={styles.applyBtn}>
          + Request Leave
        </Link>
      </div>

      {/* ── Balance cards ── */}
      <section>
        <h2 className={styles.sectionTitle}>Leave Balances</h2>
        {balances.length === 0 ? (
          <p className={styles.empty}>No leave balances found.</p>
        ) : (
          <div className={styles.balanceGrid}>
            {balances.map((b) => (
              <div key={b.leaveType} className={styles.balanceCard}>
                <p className={styles.balanceType}>{b.leaveType}</p>
                <p className={styles.balanceDays}>{b.remainingDays}</p>
                <p className={styles.balanceLabel}>days remaining</p>
              </div>
            ))}
          </div>
        )}
      </section>

      {/* ── Recent requests ── */}
      <section style={{ marginTop: 32 }}>
        <div className={styles.sectionHeader}>
          <h2 className={styles.sectionTitle} style={{ marginBottom: 0 }}>Recent Requests</h2>
          <Link to="/employee/history" className={styles.viewAll}>View all →</Link>
        </div>
        {recent.length === 0 ? (
          <p className={styles.empty}>No leave requests yet.</p>
        ) : (
          <div className={styles.tableWrap}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Type</th>
                  <th>Start</th>
                  <th>End</th>
                  <th>Days</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {recent.map((l) => (
                  <tr key={l.id}>
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
        )}
      </section>
    </div>
  )
}
