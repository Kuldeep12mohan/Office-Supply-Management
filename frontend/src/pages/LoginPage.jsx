import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import { login as loginApi } from '../services/api.js'

function LoginPage() {
  const { user, login } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ username: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (user) {
      navigate(user.role === 'ADMIN' ? '/admin' : '/employee', { replace: true })
    }
  }, [user, navigate])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    if (!form.username.trim() || !form.password.trim()) {
      setError('Username and password are required.')
      return
    }
    setLoading(true)
    try {
      const res = await loginApi(form)
      login({ username: res.data.username, role: res.data.role }, res.data.token)
      navigate(res.data.role === 'ADMIN' ? '/admin' : '/employee', { replace: true })
    } catch (err) {
      setError(err.response?.data?.error || 'Login failed. Check your credentials.')
    } finally {
      setLoading(false)
    }
  }

  const quickFill = (username, password) => {
    setForm({ username, password })
    setError('')
  }

  return (
    <div className="login-page">
      <div className="login-container">
        <div className="login-header">
          <div className="login-logo">📦</div>
          <h1 className="login-title">Office Supply Management</h1>
          <p className="login-subtitle">Sign in to your account</p>
        </div>

        <form className="login-form" onSubmit={handleSubmit}>
          {error && <div className="alert alert-error">{error}</div>}

          <div className="form-group">
            <label className="form-label">Username</label>
            <input
              type="text"
              className="form-input"
              value={form.username}
              onChange={(e) => setForm(p => ({ ...p, username: e.target.value }))}
              placeholder="Enter your username"
              autoFocus
              autoComplete="username"
            />
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password"
              className="form-input"
              value={form.password}
              onChange={(e) => setForm(p => ({ ...p, password: e.target.value }))}
              placeholder="Enter your password"
              autoComplete="current-password"
            />
          </div>

          <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>

        <div className="demo-credentials">
          <p className="demo-title">Demo Accounts</p>
          <div className="demo-grid">
            <button type="button" className="demo-btn" onClick={() => quickFill('admin', 'admin123')}>
              <span className="demo-role">Admin</span>
              <span className="demo-creds">admin / admin123</span>
            </button>
            <button type="button" className="demo-btn" onClick={() => quickFill('employee1', 'emp123')}>
              <span className="demo-role">Employee</span>
              <span className="demo-creds">employee1 / emp123</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default LoginPage
