function StatusBadge({ status }) {
  const config = {
    PENDING:  { cls: 'badge-pending',  label: '⏳ Pending' },
    APPROVED: { cls: 'badge-approved', label: '✅ Approved' },
    REJECTED: { cls: 'badge-rejected', label: '❌ Rejected' },
  }
  const { cls, label } = config[status] || { cls: 'badge-default', label: status }
  return <span className={`status-badge ${cls}`}>{label}</span>
}

export default StatusBadge
