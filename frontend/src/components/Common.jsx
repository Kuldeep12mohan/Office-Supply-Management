import React from 'react';

export const LoadingSpinner = () => (
  <div className="flex items-center justify-center min-h-screen">
    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
  </div>
);

export const ErrorMessage = ({ message }) => (
  <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
    {message}
  </div>
);

export const SuccessMessage = ({ message }) => (
  <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded">
    {message}
  </div>
);

export const Button = ({ children, variant = 'primary', ...props }) => {
  const baseClass = 'px-4 py-2 rounded font-semibold transition-colors';
  const variantClass = variant === 'primary'
    ? 'bg-blue-500 text-white hover:bg-blue-600'
    : variant === 'danger'
    ? 'bg-red-500 text-white hover:bg-red-600'
    : variant === 'success'
    ? 'bg-green-500 text-white hover:bg-green-600'
    : 'bg-gray-500 text-white hover:bg-gray-600';
  
  return (
    <button className={`${baseClass} ${variantClass}`} {...props}>
      {children}
    </button>
  );
};

export const StatusBadge = ({ status }) => {
  const colors = {
    PENDING: 'bg-yellow-100 text-yellow-800',
    APPROVED: 'bg-green-100 text-green-800',
    REJECTED: 'bg-red-100 text-red-800',
  };

  return (
    <span className={`px-3 py-1 rounded-full text-sm font-semibold ${colors[status] || colors.PENDING}`}>
      {status}
    </span>
  );
};
