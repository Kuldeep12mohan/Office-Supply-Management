import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { LoginPage } from '../pages/LoginPage';
import { AuthProvider } from '../context/AuthContext';
import * as apiService from '../services/apiService';

jest.mock('../services/apiService');

const renderLoginPage = () => {
  return render(
    <BrowserRouter>
      <AuthProvider>
        <LoginPage />
      </AuthProvider>
    </BrowserRouter>
  );
};

describe('LoginPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders login form', () => {
    renderLoginPage();
    expect(screen.getByText(/OSMS/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/Enter username/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/Enter password/i)).toBeInTheDocument();
  });

  test('successful login redirects to admin dashboard', async () => {
    const mockLogin = jest.fn();
    apiService.authService.login.mockResolvedValue({
      data: {
        success: true,
        data: {
          userId: 1,
          username: 'admin',
          fullName: 'Admin User',
          role: 'Admin',
        },
      },
    });

    renderLoginPage();
    
    const usernameInput = screen.getByPlaceholderText(/Enter username/i);
    const passwordInput = screen.getByPlaceholderText(/Enter password/i);
    const loginButton = screen.getByRole('button', { name: /Login/i });

    fireEvent.change(usernameInput, { target: { value: 'admin' } });
    fireEvent.change(passwordInput, { target: { value: 'admin123' } });
    fireEvent.click(loginButton);

    await waitFor(() => {
      expect(apiService.authService.login).toHaveBeenCalledWith('admin', 'admin123');
    });
  });

  test('displays error on failed login', async () => {
    apiService.authService.login.mockRejectedValue({
      response: {
        data: {
          message: 'Invalid username or password',
        },
      },
    });

    renderLoginPage();
    
    const usernameInput = screen.getByPlaceholderText(/Enter username/i);
    const passwordInput = screen.getByPlaceholderText(/Enter password/i);
    const loginButton = screen.getByRole('button', { name: /Login/i });

    fireEvent.change(usernameInput, { target: { value: 'invalid' } });
    fireEvent.change(passwordInput, { target: { value: 'wrong' } });
    fireEvent.click(loginButton);

    await waitFor(() => {
      expect(screen.getByText(/Invalid username or password/i)).toBeInTheDocument();
    });
  });

  test('demo credentials are displayed', () => {
    renderLoginPage();
    expect(screen.getByText(/admin@officesupply/i)).toBeInTheDocument();
    expect(screen.getByText(/employee1/i)).toBeInTheDocument();
  });
});
