import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export const Sidebar = () => {
  const { user } = useAuth();

  return (
    <aside className="bg-gray-800 text-white w-64 min-h-screen">
      <nav className="p-4">
        {user?.role === 'Admin' ? (
          <>
            <h2 className="text-lg font-bold mb-4">Admin Menu</h2>
            <Link to="/admin/dashboard" className="block py-2 px-4 hover:bg-gray-700 rounded mb-2">
              Dashboard
            </Link>
            <Link to="/admin/pending-requests" className="block py-2 px-4 hover:bg-gray-700 rounded mb-2">
              Pending Requests
            </Link>
            <Link to="/admin/inventory" className="block py-2 px-4 hover:bg-gray-700 rounded mb-2">
              Inventory Management
            </Link>
            <Link to="/admin/history" className="block py-2 px-4 hover:bg-gray-700 rounded mb-2">
              Request History
            </Link>
            <Link to="/admin/users" className="block py-2 px-4 hover:bg-gray-700 rounded mb-2">
              Users
            </Link>
          </>
        ) : (
          <>
            <h2 className="text-lg font-bold mb-4">Employee Menu</h2>
            <Link to="/employee/dashboard" className="block py-2 px-4 hover:bg-gray-700 rounded mb-2">
              Dashboard
            </Link>
            <Link to="/employee/submit-request" className="block py-2 px-4 hover:bg-gray-700 rounded mb-2">
              Submit Request
            </Link>
            <Link to="/employee/my-requests" className="block py-2 px-4 hover:bg-gray-700 rounded mb-2">
              My Requests
            </Link>
          </>
        )}
      </nav>
    </aside>
  );
};
