import { useState, useEffect, useCallback } from 'react'
import Navbar from '../components/Navbar.jsx'
import StatusBadge from '../components/StatusBadge.jsx'
import { getInventory, addInventoryItem, updateInventoryItem, getRequests, approveRequest, rejectRequest } from '../services/api.js'

const INIT_INV = { name: '', quantity: '', description: '' }

function AdminDashboard() {
  const [activeTab, setActiveTab] = useState('requests')
  const [requests, setRequests] = useState([])
  const [inventory, setInventory] = useState([])
  const [loading, setLoading] = useState(true)
  const [statusFilter, setStatusFilter] = useState('ALL')
  const [toast, setToast] = useState({ type: '', message: '' })
  const [rejectModal, setRejectModal] = useState({ open: false, requestId: null, reason: '' })
  const [invModal, setInvModal] = useState({ open: false, editItem: null })
  const [invForm, setInvForm] = useState(INIT_INV)
  const [invErrors, setInvErrors] = useState({})

  const showToast = (type, message) => {
    setToast({ type, message })
    setTimeout(() => setToast({ type: '', message: '' }), 4500)
  }

  const fetchAll = useCallback(async () => {
    setLoading(true)
    try {
      const [reqRes, invRes] = await Promise.all([getRequests(), getInventory()])
      setRequests(reqRes.data)
      setInventory(invRes.data)
    } catch {
      showToast('error', 'Failed to load data.')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { fetchAll() }, [fetchAll])

  const handleApprove = async (id) => {
    try {
      await approveRequest(id)
      showToast('success', '✅ Request approved and inventory updated.')
      await fetchAll()
    } catch (err) {
      showToast('error', `❌ ${err.response?.data?.error || 'Failed to approve.'}`)
    }
  }

  const closeRejectModal = () => setRejectModal({ open: false, requestId: null, reason: '' })

  const handleRejectConfirm = async () => {
    try {
      await rejectRequest(rejectModal.requestId, { reason: rejectModal.reason })
      closeRejectModal()
      showToast('success', '🚫 Request rejected.')
      await fetchAll()
    } catch (err) {
      showToast('error', `❌ ${err.response?.data?.error || 'Failed to reject.'}`)
    }
  }

  const openEditInv = (item) => {
    setInvForm({ name: item.name, quantity: String(item.quantity), description: item.description || '' })
    setInvErrors({})
    setInvModal({ open: true, editItem: item })
  }

  const closeInvModal = () => {
    setInvModal({ open: false, editItem: null })
    setInvForm(INIT_INV)
    setInvErrors({})
  }

  const handleInvSubmit = async () => {
    const errs = {}
    if (!invForm.name.trim()) errs.name = 'Name is required.'
    if (invForm.quantity === '' || parseInt(invForm.quantity) < 0) errs.quantity = 'Quantity must be 0 or greater.'
    if (Object.keys(errs).length > 0) { setInvErrors(errs); return }
    const payload = {
      name: invForm.name.trim(),
      quantity: parseInt(invForm.quantity),
      description: invForm.description.trim() || null,
    }
    try {
      if (invModal.editItem) {
        await updateInventoryItem(invModal.editItem.id, payload)
        showToast('success', '✅ Item updated.')
      } else {
        await addInventoryItem(payload)
        showToast('success', '✅ Item added.')
      }
      closeInvModal()
      await fetchAll()
    } catch (err) {
      showToast('error', `❌ ${err.response?.data?.error || 'Failed to save.'}`)
    }
  }

  const formatDate = (d) => d ? new Date(d).toLocaleString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit'
  }) : '—'

  const stockLevel = (q) =>
    q <= 5 ? { label: 'Critical', cls: 'stock-critical' }
    : q <= 20 ? { label: 'Low', cls: 'stock-low' }
    : q <= 50 ? { label: 'Medium', cls: 'stock-medium' }
    : { label: 'Good', cls: 'stock-good' }

  const filtered = requests.filter(r => statusFilter === 'ALL' || r.status === statusFilter)
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
          <h1>Admin Dashboard</h1>
          <p>Manage inventory and review supply requests.</p>
        </div>

        <div className="stats-grid">
          {[
            ['📋', stats.total, 'Total Requests', 'stat-total'],
            ['⏳', stats.pending, 'Pending', 'stat-pending'],
            ['✅', stats.approved, 'Approved', 'stat-approved'],
            ['❌', stats.rejected, 'Rejected', 'stat-rejected'],
          ].map(([icon, n, label, cls]) => (
            <div key={label} className={`stat-card ${cls}`}>
              <div className="stat-icon">{icon}</div>
              <div className="stat-info">
                <div className="stat-number">{n}</div>
                <div className="stat-label">{label}</div>
              </div>
            </div>
          ))}
        </div>

        <div className="tab-bar">
          <button className={`tab-btn ${activeTab === 'requests' ? 'tab-btn-active' : ''}`} onClick={() => setActiveTab('requests')}>
            📋 Supply Requests
            {stats.pending > 0 && <span className="tab-badge">{stats.pending}</span>}
          </button>
          <button className={`tab-btn ${activeTab === 'inventory' ? 'tab-btn-active' : ''}`} onClick={() => setActiveTab('inventory')}>
            📦 Inventory
            <span className="tab-badge tab-badge-neutral">{inventory.length}</span>
          </button>
        </div>

        {activeTab === 'requests' && (
          <div className="card">
            <div className="card-header">
              <h2>Supply Requests</h2>
              <div className="header-controls">
                <label className="filter-label">Status:</label>
                <select className="select-input" value={statusFilter} onChange={e => setStatusFilter(e.target.value)}>
                  <option value="ALL">All ({stats.total})</option>
                  <option value="PENDING">Pending ({stats.pending})</option>
                  <option value="APPROVED">Approved ({stats.approved})</option>
                  <option value="REJECTED">Rejected ({stats.rejected})</option>
                </select>
              </div>
            </div>
            <div className="card-body p-0">
              {loading ? (
                <div className="state-box"><div className="spinner" /><p>Loading...</p></div>
              ) : filtered.length === 0 ? (
                <div className="state-box"><div className="empty-icon">📭</div><p>No requests found.</p></div>
              ) : (
                <div className="table-scroll">
                  <table className="table">
                    <thead>
                      <tr>
                        <th>#</th><th>Employee</th><th>Item</th><th>Qty</th>
                        <th>Remarks</th><th>Status</th><th>Date</th><th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {filtered.map(req => (
                        <tr key={req.id} className={req.status === 'PENDING' ? 'row-pending' : ''}>
                          <td className="text-muted text-sm">{req.id}</td>
                          <td>
                            <div className="user-cell">
                              <span className="user-avatar-sm">{req.employeeUsername?.charAt(0).toUpperCase()}</span>
                              {req.employeeUsername}
                            </div>
                          </td>
                          <td><strong>{req.itemName}</strong></td>
                          <td>{req.quantity}</td>
                          <td className="text-sm text-muted">{req.remarks || '—'}</td>
                          <td>
                            <StatusBadge status={req.status} />
                            {req.rejectionReason && (
                              <div className="rejection-note text-sm">{req.rejectionReason}</div>
                            )}
                          </td>
                          <td className="text-sm text-muted nowrap">{formatDate(req.createdAt)}</td>
                          <td>
                            {req.status === 'PENDING' ? (
                              <div className="action-group">
                                <button className="btn btn-success btn-sm" onClick={() => handleApprove(req.id)}>✓ Approve</button>
                                <button className="btn btn-danger btn-sm" onClick={() => setRejectModal({ open: true, requestId: req.id, reason: '' })}>✕ Reject</button>
                              </div>
                            ) : <span className="text-muted text-sm">—</span>}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        )}

        {activeTab === 'inventory' && (
          <div className="card">
            <div className="card-header">
              <h2>Current Inventory</h2>
              <button className="btn btn-primary btn-sm" onClick={() => { setInvForm(INIT_INV); setInvErrors({}); setInvModal({ open: true, editItem: null }) }}>
                + Add Item
              </button>
            </div>
            <div className="card-body p-0">
              {loading ? (
                <div className="state-box"><div className="spinner" /></div>
              ) : inventory.length === 0 ? (
                <div className="state-box"><div className="empty-icon">📦</div><p>No inventory items.</p></div>
              ) : (
                <div className="table-scroll">
                  <table className="table">
                    <thead>
                      <tr><th>#</th><th>Item Name</th><th>Quantity</th><th>Description</th><th>Stock Level</th><th>Actions</th></tr>
                    </thead>
                    <tbody>
                      {inventory.map(item => {
                        const stock = stockLevel(item.quantity)
                        return (
                          <tr key={item.id}>
                            <td className="text-muted text-sm">{item.id}</td>
                            <td><strong>{item.name}</strong></td>
                            <td><span className={item.quantity <= 5 ? 'text-danger fw-bold' : ''}>{item.quantity}</span></td>
                            <td className="text-sm text-muted">{item.description || '—'}</td>
                            <td><span className={`stock-badge ${stock.cls}`}>{stock.label}</span></td>
                            <td><button className="btn btn-secondary btn-sm" onClick={() => openEditInv(item)}>✎ Edit</button></td>
                          </tr>
                        )
                      })}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        )}
      </main>

      {rejectModal.open && (
        <div className="modal-overlay" onClick={closeRejectModal}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h3>🚫 Reject Request</h3>
              <button className="modal-close-btn" onClick={closeRejectModal}>✕</button>
            </div>
            <div className="modal-body">
              <p className="modal-desc">Optionally provide a reason for rejection.</p>
              <div className="form-group">
                <label className="form-label">Rejection Reason (optional)</label>
                <textarea className="form-input" rows={3} value={rejectModal.reason}
                  onChange={e => setRejectModal(p => ({ ...p, reason: e.target.value }))}
                  placeholder="e.g., Budget constraints, item already available..." autoFocus />
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-ghost" onClick={closeRejectModal}>Cancel</button>
              <button className="btn btn-danger" onClick={handleRejectConfirm}>Confirm Rejection</button>
            </div>
          </div>
        </div>
      )}

      {invModal.open && (
        <div className="modal-overlay" onClick={closeInvModal}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h3>{invModal.editItem ? '✎ Edit Inventory Item' : '+ Add Inventory Item'}</h3>
              <button className="modal-close-btn" onClick={closeInvModal}>✕</button>
            </div>
            <div className="modal-body">
              <div className="form-group">
                <label className="form-label">Item Name <span className="required">*</span></label>
                <input type="text" className={`form-input ${invErrors.name ? 'input-error' : ''}`}
                  value={invForm.name}
                  onChange={e => { setInvForm(p => ({ ...p, name: e.target.value })); if (invErrors.name) setInvErrors(p => ({ ...p, name: '' })) }}
                  placeholder="e.g., Pens, Notebooks" autoFocus />
                {invErrors.name && <span className="field-error">{invErrors.name}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">Quantity <span className="required">*</span></label>
                <input type="number" className={`form-input ${invErrors.quantity ? 'input-error' : ''}`}
                  value={invForm.quantity} min="0"
                  onChange={e => { setInvForm(p => ({ ...p, quantity: e.target.value })); if (invErrors.quantity) setInvErrors(p => ({ ...p, quantity: '' })) }}
                  placeholder="Enter quantity" />
                {invErrors.quantity && <span className="field-error">{invErrors.quantity}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">Description <span className="optional">(optional)</span></label>
                <input type="text" className="form-input" value={invForm.description}
                  onChange={e => setInvForm(p => ({ ...p, description: e.target.value }))}
                  placeholder="Brief description" />
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-ghost" onClick={closeInvModal}>Cancel</button>
              <button className="btn btn-primary" onClick={handleInvSubmit}>
                {invModal.editItem ? 'Save Changes' : 'Add Item'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default AdminDashboard
