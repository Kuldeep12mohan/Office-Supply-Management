import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authService } from '../services/apiService';

export const Header = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await authService.logout();
    } catch (err) {
      console.error('Logout error:', err);
    }
    logout();
    navigate('/login');
  };

  return (
    <header className="bg-blue-600 text-white shadow-md">
      <div className="max-w-7xl mx-auto px-6 py-4 flex justify-between items-center">
        <div className="flex items-center gap-2">
          <h1 className="text-2xl font-bold">OSMS</h1>
          <p className="text-sm opacity-80">Office Supply Management</p>
        </div>

        <div className="flex items-center gap-4">
          <span className="text-sm">
            {user?.fullName} <span className="opacity-80">({user?.role})</span>
          </span>
          <button
            onClick={handleLogout}
            className="bg-red-500 hover:bg-red-600 px-4 py-2 rounded transition-colors"
          >
            Logout
          </button>
        </div>
      </div>
    </header>
  );
};
