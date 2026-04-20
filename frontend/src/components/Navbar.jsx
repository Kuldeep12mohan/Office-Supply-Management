import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <nav className="navbar">
      <a className="navbar-brand" href="#">
        <span className="navbar-logo">📦</span>
        <span className="navbar-title">Office Supply Management</span>
      </a>
      <div className="navbar-right">
        {user && (
          <div className="navbar-user-info">
            <div className="navbar-avatar">
              {user.username?.charAt(0).toUpperCase()}
            </div>
            <div className="navbar-user-details">
              <span className="navbar-username">{user.username}</span>
              <span className={`navbar-role-badge ${user.role === 'ADMIN' ? 'role-admin' : 'role-employee'}`}>
                {user.role}
              </span>
            </div>
          </div>
        )}
        <button className="btn-logout" onClick={handleLogout}>
          Logout
        </button>
      </div>
    </nav>
  )
}

export default Navbar
