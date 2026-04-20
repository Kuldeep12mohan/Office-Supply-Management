import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { requestService } from '../services/apiService';
import { LoadingSpinner, SuccessMessage, ErrorMessage, StatusBadge } from '../components/Common';

export const EmployeeDashboard = () => {
  const { user } = useAuth();
  const [requests, setRequests] = useState([]);
  const [stats, setStats] = useState({
    total: 0,
    pending: 0,
    approved: 0,
    rejected: 0,
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadRequests = async () => {
      try {
        const response = await requestService.getMyRequests();
        const allRequests = response.data.data;
        setRequests(allRequests);

        const newStats = {
          total: allRequests.length,
          pending: allRequests.filter(r => r.status === 'PENDING').length,
          approved: allRequests.filter(r => r.status === 'APPROVED').length,
          rejected: allRequests.filter(r => r.status === 'REJECTED').length,
        };
        setStats(newStats);
      } catch (err) {
        setError('Failed to load requests');
      } finally {
        setLoading(false);
      }
    };

    loadRequests();
  }, []);

  if (loading) return <LoadingSpinner />;

  return (
    <div className="p-6">
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-gray-800">Welcome, {user?.fullName}!</h1>
        <p className="text-gray-600">Here's your supply request overview</p>
      </div>

      {error && <ErrorMessage message={error} />}

      <div className="grid grid-cols-4 gap-4 mb-8">
        <StatCard label="Total Requests" value={stats.total} color="bg-blue-500" />
        <StatCard label="Pending" value={stats.pending} color="bg-yellow-500" />
        <StatCard label="Approved" value={stats.approved} color="bg-green-500" />
        <StatCard label="Rejected" value={stats.rejected} color="bg-red-500" />
      </div>

      <div className="bg-white rounded shadow-md p-6">
        <h2 className="text-2xl font-bold mb-4">Recent Requests</h2>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b">
                <th className="text-left py-2">Item</th>
                <th className="text-left py-2">Quantity</th>
                <th className="text-left py-2">Status</th>
                <th className="text-left py-2">Date</th>
              </tr>
            </thead>
            <tbody>
              {requests.map(req => (
                <tr key={req.id} className="border-b hover:bg-gray-50">
                  <td className="py-3">{req.itemName}</td>
                  <td className="py-3">{req.quantity}</td>
                  <td className="py-3"><StatusBadge status={req.status} /></td>
                  <td className="py-3">{new Date(req.createdAt).toLocaleDateString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

const StatCard = ({ label, value, color }) => (
  <div className={`${color} rounded shadow p-6 text-white`}>
    <p className="text-sm opacity-80">{label}</p>
    <p className="text-3xl font-bold">{value}</p>
  </div>
);
