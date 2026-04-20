import React from 'react';
import { render, screen } from '@testing-library/react';
import { ErrorMessage, SuccessMessage, StatusBadge, Button } from '../components/Common';

describe('Common Components', () => {
  describe('ErrorMessage', () => {
    test('renders error message', () => {
      render(<ErrorMessage message="Test error" />);
      expect(screen.getByText('Test error')).toBeInTheDocument();
    });

    test('has error styling', () => {
      const { container } = render(<ErrorMessage message="Test error" />);
      const errorDiv = container.firstChild;
      expect(errorDiv).toHaveClass('bg-red-100');
    });
  });

  describe('SuccessMessage', () => {
    test('renders success message', () => {
      render(<SuccessMessage message="Success!" />);
      expect(screen.getByText('Success!')).toBeInTheDocument();
    });

    test('has success styling', () => {
      const { container } = render(<SuccessMessage message="Success!" />);
      const successDiv = container.firstChild;
      expect(successDiv).toHaveClass('bg-green-100');
    });
  });

  describe('StatusBadge', () => {
    test('renders PENDING status', () => {
      render(<StatusBadge status="PENDING" />);
      expect(screen.getByText('PENDING')).toBeInTheDocument();
    });

    test('renders APPROVED status', () => {
      render(<StatusBadge status="APPROVED" />);
      expect(screen.getByText('APPROVED')).toBeInTheDocument();
    });

    test('renders REJECTED status', () => {
      render(<StatusBadge status="REJECTED" />);
      expect(screen.getByText('REJECTED')).toBeInTheDocument();
    });

    test('has correct color for PENDING', () => {
      const { container } = render(<StatusBadge status="PENDING" />);
      const badge = container.firstChild;
      expect(badge).toHaveClass('bg-yellow-100');
    });

    test('has correct color for APPROVED', () => {
      const { container } = render(<StatusBadge status="APPROVED" />);
      const badge = container.firstChild;
      expect(badge).toHaveClass('bg-green-100');
    });
  });

  describe('Button', () => {
    test('renders button with text', () => {
      render(<Button>Click me</Button>);
      expect(screen.getByRole('button', { name: /Click me/i })).toBeInTheDocument();
    });

    test('applies primary variant styling', () => {
      const { container } = render(<Button variant="primary">Primary</Button>);
      const button = container.querySelector('button');
      expect(button).toHaveClass('bg-blue-500');
    });

    test('applies danger variant styling', () => {
      const { container } = render(<Button variant="danger">Delete</Button>);
      const button = container.querySelector('button');
      expect(button).toHaveClass('bg-red-500');
    });

    test('handles click events', () => {
      const handleClick = jest.fn();
      render(<Button onClick={handleClick}>Click</Button>);
      const button = screen.getByRole('button');
      button.click();
      expect(handleClick).toHaveBeenCalledTimes(1);
    });
  });
});
