import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext.jsx'
import PrivateRoute from './components/PrivateRoute.jsx'
import LoginPage from './pages/LoginPage.jsx'
import EmployeeDashboard from './pages/EmployeeDashboard.jsx'
import AdminDashboard from './pages/AdminDashboard.jsx'

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/employee" element={
            <PrivateRoute role="EMPLOYEE"><EmployeeDashboard /></PrivateRoute>
          } />
          <Route path="/admin" element={
            <PrivateRoute role="ADMIN"><AdminDashboard /></PrivateRoute>
          } />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}

export default App
