import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

function PrivateRoute({ children, role }) {
  const { user, loading } = useAuth()

  if (loading) {
    return (
      <div className="loading-screen">
        <div className="spinner" />
        <p>Loading...</p>
      </div>
    )
  }

  if (!user) return <Navigate to="/login" replace />
  if (role && user.role !== role) {
    return <Navigate to={user.role === 'ADMIN' ? '/admin' : '/employee'} replace />
  }
  return children
}

export default PrivateRoute
