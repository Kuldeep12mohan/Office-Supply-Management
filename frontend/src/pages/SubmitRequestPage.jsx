import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { requestService } from '../services/apiService';
import { Button, ErrorMessage, LoadingSpinner, SuccessMessage } from '../components/Common';

export const SubmitRequestPage = () => {
  const [formData, setFormData] = useState({
    itemName: '',
    quantity: 1,
    remarks: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'quantity' ? parseInt(value) : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    try {
      await requestService.createRequest(formData);
      setSuccess('Request submitted successfully!');
      setTimeout(() => navigate('/employee/my-requests'), 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit request');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="p-6 max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold mb-6">Submit Supply Request</h1>

      {error && <ErrorMessage message={error} />}
      {success && <SuccessMessage message={success} />}

      <form onSubmit={handleSubmit} className="bg-white rounded shadow-md p-6 space-y-4">
        <div>
          <label className="block text-gray-700 font-semibold mb-2">Item Name *</label>
          <input
            type="text"
            name="itemName"
            value={formData.itemName}
            onChange={handleChange}
            className="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:border-blue-500"
            placeholder="e.g., Printer Paper A4"
            required
          />
        </div>

        <div>
          <label className="block text-gray-700 font-semibold mb-2">Quantity *</label>
          <input
            type="number"
            name="quantity"
            value={formData.quantity}
            onChange={handleChange}
            min="1"
            className="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:border-blue-500"
            required
          />
        </div>

        <div>
          <label className="block text-gray-700 font-semibold mb-2">Remarks (Optional)</label>
          <textarea
            name="remarks"
            value={formData.remarks}
            onChange={handleChange}
            className="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:border-blue-500"
            placeholder="Add any additional comments"
            rows="4"
          />
        </div>

        <div className="flex gap-3">
          <Button type="submit" variant="primary">Submit Request</Button>
          <Button type="button" variant="secondary" onClick={() => navigate(-1)}>Cancel</Button>
        </div>
      </form>
    </div>
  );
};
