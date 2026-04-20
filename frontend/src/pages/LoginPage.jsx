import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/apiService';
import { useAuth } from '../context/AuthContext';
import { ErrorMessage, Button, LoadingSpinner } from '../components/Common';

export const LoginPage = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await authService.login(username, password);
      const userData = response.data.data;
      login(userData);
      navigate(userData.role === 'Admin' ? '/admin/dashboard' : '/employee/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-blue-500 to-blue-700">
      <div className="bg-white rounded-lg shadow-lg p-8 w-96">
        <h1 className="text-3xl font-bold text-center mb-2 text-gray-800">OSMS</h1>
        <p className="text-center text-gray-600 mb-6">Office Supply Management System</p>
        
        {error && <ErrorMessage message={error} />}

        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-gray-700 font-semibold mb-2">Username</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:border-blue-500"
              placeholder="Enter username"
              required
            />
          </div>

          <div>
            <label className="block text-gray-700 font-semibold mb-2">Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:border-blue-500"
              placeholder="Enter password"
              required
            />
          </div>

          <Button className="w-full" onClick={handleLogin} variant="primary">
            Login
          </Button>
        </form>

        <div className="mt-6 text-center text-sm text-gray-600">
          <p>Demo Credentials:</p>
          <p><strong>Admin:</strong> admin / admin123</p>
          <p><strong>Employee:</strong> employee1 / emp123</p>
        </div>
      </div>
    </div>
  );
};
