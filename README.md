# 📦 Office Supply Management System

A full-stack web application for managing office supply requests with role-based access control.

## Features

- 🔐 **JWT Authentication** with role-based access (Admin / Employee)
- 📝 **Supply Requests** — Employees submit requests (item, quantity, remarks)
- ✅ **Approval Workflow** — Admin approves or rejects with optional reason
- 📦 **Inventory Management** — Auto-decrements on approval; Admin can add/edit items
- 📋 **Request History** — Full audit trail with status tracking
- 🌱 **Seed Data** — Admin user + 7 inventory items on startup

## Tech Stack

| Layer    | Technology                          |
|----------|-------------------------------------|
| Backend  | Java 17, Spring Boot 3.2, Maven     |
| Security | Spring Security 6, JWT              |
| Database | H2 In-Memory                        |
| Frontend | React 18, Vite 5, React Router v6   |

## Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 18+

## Running the Application

### Backend
```bash
cd backend
mvn spring-boot:run
```
API available at http://localhost:8080

### Frontend
```bash
cd frontend
npm install
npm run dev
```
App available at http://localhost:5173

## Default Credentials

| Role     | Username  | Password  |
|----------|-----------|-----------|
| Admin    | admin     | admin123  |
| Employee | employee1 | emp123    |

## H2 Console

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:officesupplydb`
- Username: `sa` | Password: *(empty)*

## API Endpoints

| Method | Path                       | Description                      |
|--------|----------------------------|----------------------------------|
| POST   | /api/auth/login            | Login, returns JWT               |
| GET    | /api/inventory             | List inventory (Admin)           |
| POST   | /api/inventory             | Add inventory item (Admin)       |
| PUT    | /api/inventory/{id}        | Update inventory item (Admin)    |
| GET    | /api/requests              | Get requests (role-filtered)     |
| POST   | /api/requests              | Submit request (Employee)        |
| PUT    | /api/requests/{id}/approve | Approve request (Admin)          |
| PUT    | /api/requests/{id}/reject  | Reject request (Admin)           |

## Architecture

See [ARCHITECTURE.md](./ARCHITECTURE.md) for detailed system design and Mermaid diagrams.
