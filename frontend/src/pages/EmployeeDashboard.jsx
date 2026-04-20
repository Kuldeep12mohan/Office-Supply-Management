import { useState, useEffect, useCallback } from 'react'
import Navbar from '../components/Navbar.jsx'
import StatusBadge from '../components/StatusBadge.jsx'
import { getRequests, createRequest } from '../services/api.js'

const INIT_FORM = { itemName: '', quantity: '', remarks: '' }

function EmployeeDashboard() {
  const [requests, setRequests] = useState([])
  const [loading, setLoading] = useState(true)
  const [form, setForm] = useState(INIT_FORM)
  const [errors, setErrors] = useState({})
  const [submitting, setSubmitting] = useState(false)
  const [toast, setToast] = useState({ type: '', message: '' })

  const showToast = (type, message) => {
    setToast({ type, message })
    setTimeout(() => setToast({ type: '', message: '' }), 4000)
  }

  const fetchRequests = useCallback(async () => {
    setLoading(true)
    try {
      const res = await getRequests()
      setRequests(res.data)
    } catch {
      showToast('error', 'Failed to load requests.')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { fetchRequests() }, [fetchRequests])

  const validate = () => {
    const errs = {}
    if (!form.itemName.trim()) errs.itemName = 'Item name is required.'
    if (!form.quantity || parseInt(form.quantity) < 1) errs.quantity = 'Quantity must be at least 1.'
    return errs
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setErrors({})
    const errs = validate()
    if (Object.keys(errs).length > 0) { setErrors(errs); return }
    setSubmitting(true)
    try {
      await createRequest({
        itemName: form.itemName.trim(),
        quantity: parseInt(form.quantity),
        remarks: form.remarks.trim() || null,
      })
      setForm(INIT_FORM)
      showToast('success', '✅ Request submitted successfully!')
      await fetchRequests()
    } catch (err) {
      const msg = err.response?.data?.error || 'Failed to submit request.'
      showToast('error', `❌ ${msg}`)
    } finally {
      setSubmitting(false)
    }
  }

  const formatDate = (d) => d ? new Date(d).toLocaleString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit'
  }) : '—'

  const stats = {
    total: requests.length,
    pending: requests.filter(r => r.status === 'PENDING').length,
    approved: requests.filter(r => r.status === 'APPROVED').length,
    rejected: requests.filter(r => r.status === 'REJECTED').length,
  }

  return (
    <div className="page">
      <Navbar />
      {toast.message && <div className={`toast toast-${toast.type}`}>{toast.message}</div>}
      <main className="page-content">
        <div className="page-header">
          <h1>Employee Dashboard</h1>
          <p>Submit supply requests and track their status.</p>
        </div>

        <div className="stats-grid">
          <div className="stat-card stat-total">
            <div className="stat-icon">📋</div>
            <div className="stat-info"><div className="stat-number">{stats.total}</div><div className="stat-label">Total</div></div>
          </div>
          <div className="stat-card stat-pending">
            <div className="stat-icon">⏳</div>
            <div className="stat-info"><div className="stat-number">{stats.pending}</div><div className="stat-label">Pending</div></div>
          </div>
          <div className="stat-card stat-approved">
            <div className="stat-icon">✅</div>
            <div className="stat-info"><div className="stat-number">{stats.approved}</div><div className="stat-label">Approved</div></div>
          </div>
          <div className="stat-card stat-rejected">
            <div className="stat-icon">❌</div>
            <div className="stat-info"><div className="stat-number">{stats.rejected}</div><div className="stat-label">Rejected</div></div>
          </div>
        </div>

        <div className="two-col-layout">
          <div className="card">
            <div className="card-header"><h2>New Supply Request</h2></div>
            <div className="card-body">
              <form onSubmit={handleSubmit}>
                <div className="form-group">
                  <label className="form-label">Item Name <span className="required">*</span></label>
                  <input type="text" className={`form-input ${errors.itemName ? 'input-error' : ''}`}
                    value={form.itemName}
                    onChange={e => { setForm(p => ({ ...p, itemName: e.target.value })); if (errors.itemName) setErrors(p => ({ ...p, itemName: '' })) }}
                    placeholder="e.g., Notebooks, Pens, Staplers" />
                  {errors.itemName && <span className="field-error">{errors.itemName}</span>}
                </div>
                <div className="form-group">
                  <label className="form-label">Quantity <span className="required">*</span></label>
                  <input type="number" className={`form-input ${errors.quantity ? 'input-error' : ''}`}
                    value={form.quantity} min="1"
                    onChange={e => { setForm(p => ({ ...p, quantity: e.target.value })); if (errors.quantity) setErrors(p => ({ ...p, quantity: '' })) }}
                    placeholder="Enter quantity" />
                  {errors.quantity && <span className="field-error">{errors.quantity}</span>}
                </div>
                <div className="form-group">
                  <label className="form-label">Remarks <span className="optional">(optional)</span></label>
                  <textarea className="form-input" rows={3}
                    value={form.remarks}
                    onChange={e => setForm(p => ({ ...p, remarks: e.target.value }))}
                    placeholder="Additional notes or urgency..." />
                </div>
                <button type="submit" className="btn btn-primary btn-block" disabled={submitting}>
                  {submitting ? 'Submitting...' : '📨 Submit Request'}
                </button>
              </form>
            </div>
          </div>

          <div className="card">
            <div className="card-header">
              <h2>My Requests</h2>
              <button className="btn btn-ghost btn-sm" onClick={fetchRequests}>↻ Refresh</button>
            </div>
            <div className="card-body p-0">
              {loading ? (
                <div className="state-box"><div className="spinner" /><p>Loading...</p></div>
              ) : requests.length === 0 ? (
                <div className="state-box">
                  <div className="empty-icon">📭</div>
                  <p>No requests yet. Submit your first request!</p>
                </div>
              ) : (
                <div className="request-list">
                  {requests.map(req => (
                    <div key={req.id} className="request-item">
                      <div className="request-item-top">
                        <div className="request-item-name">{req.itemName}</div>
                        <StatusBadge status={req.status} />
                      </div>
                      <div className="request-item-meta">
                        <span>Qty: <strong>{req.quantity}</strong></span>
                        <span className="meta-sep">•</span>
                        <span>{formatDate(req.createdAt)}</span>
                      </div>
                      {req.remarks && <div className="request-remarks">💬 {req.remarks}</div>}
                      {req.rejectionReason && (
                        <div className="rejection-note">⚠️ {req.rejectionReason}</div>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      </main>
    </div>
  )
}

export default EmployeeDashboard
