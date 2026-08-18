import { NavLink } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import styles from './Sidebar.module.css'

const EMP_LINKS = [
  { to: '/employee', label: 'Dashboard', icon: '🏠' },
  { to: '/employee/apply', label: 'Apply Leave', icon: '📋' },
  { to: '/employee/history', label: 'My Leaves', icon: '📅' },
]

const ADMIN_LINKS = [
  { to: '/admin', label: 'Dashboard', icon: '🏠' },
  { to: '/admin/leaves', label: 'Leave Requests', icon: '📋' },
  { to: '/admin/users', label: 'Users', icon: '👥' },
  { to: '/admin/leave-types', label: 'Leave Types', icon: '⚙️' },
]

export default function Sidebar() {
  const { user, logout } = useAuth()
  const links = user?.role === 'ADMIN' ? ADMIN_LINKS : EMP_LINKS

  return (
    <aside className={styles.sidebar}>
      {/* Logo */}
      <div className={styles.logo}>
        <span className={styles.logoIcon}>L</span>
        <span className={styles.logoText}>LMS</span>
      </div>

      {/* Nav */}
      <nav className={styles.nav}>
        {links.map(({ to, label, icon }) => (
          <NavLink
            key={to}
            to={to}
            end={to.endsWith('/employee') || to.endsWith('/admin')}
            className={({ isActive }) =>
              `${styles.link} ${isActive ? styles.active : ''}`
            }
          >
            <span className={styles.icon}>{icon}</span>
            {label}
          </NavLink>
        ))}
      </nav>

      {/* User chip + logout */}
      <div className={styles.footer}>
        <div className={styles.userChip}>
          <span className={styles.avatar}>
            {user?.username?.charAt(0).toUpperCase()}
          </span>
          <div className={styles.userInfo}>
            <span className={styles.username}>{user?.username}</span>
            <span className={styles.role}>{user?.role}</span>
          </div>
        </div>
        <button className={styles.logoutBtn} onClick={logout}>
          Sign out
        </button>
      </div>
    </aside>
  )
}
