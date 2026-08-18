import styles from './StatusBadge.module.css'

const MAP = {
  PENDING:  { cls: 'pending',  label: 'Pending'  },
  APPROVED: { cls: 'approved', label: 'Approved' },
  REJECTED: { cls: 'rejected', label: 'Rejected' },
}

export default function StatusBadge({ status }) {
  const { cls, label } = MAP[status] ?? { cls: 'pending', label: status }
  return <span className={`${styles.badge} ${styles[cls]}`}>{label}</span>
}
