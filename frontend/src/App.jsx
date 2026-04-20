import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { LoginPage } from './pages/LoginPage';
import { EmployeeDashboard } from './pages/EmployeeDashboard';
import { SubmitRequestPage } from './pages/SubmitRequestPage';
import { Header } from './components/Header';
import { Sidebar } from './components/Sidebar';
import { ProtectedRoute } from './utils/ProtectedRoute';
import { LoadingSpinner } from './components/Common';

function AppLayout() {
  const { user, loading } = useAuth();

  if (loading) return <LoadingSpinner />;

  if (!user) {
    return <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="*" element={<Navigate to="/login" />} />
    </Routes>;
  }

  return (
    <div className="flex flex-col h-screen">
      <Header />
      <div className="flex flex-1 overflow-hidden">
        <Sidebar />
        <main className="flex-1 overflow-auto">
          <Routes>
            {/* Employee Routes */}
            <Route path="/employee/dashboard" element={
              <ProtectedRoute requiredRole="Employee">
                <EmployeeDashboard />
              </ProtectedRoute>
            } />
            <Route path="/employee/submit-request" element={
              <ProtectedRoute requiredRole="Employee">
                <SubmitRequestPage />
              </ProtectedRoute>
            } />

            {/* Admin Routes */}
            <Route path="/admin/dashboard" element={
              <ProtectedRoute requiredRole="Admin">
                <div className="p-6"><h1 className="text-3xl font-bold">Admin Dashboard (Coming Soon)</h1></div>
              </ProtectedRoute>
            } />

            {/* Catch all */}
            <Route path="/" element={<Navigate to={user.role === 'Admin' ? '/admin/dashboard' : '/employee/dashboard'} />} />
            <Route path="*" element={<Navigate to={user.role === 'Admin' ? '/admin/dashboard' : '/employee/dashboard'} />} />
          </Routes>
        </main>
      </div>
    </div>
  );
}

function App() {
  return (
    <Router>
      <AuthProvider>
        <AppLayout />
      </AuthProvider>
    </Router>
  );
}

export default App;
