# Office Supply Management System — Architecture

## Overview

A full-stack web application for managing office supply requests with role-based access control. Employees submit supply requests; Admins review, approve, or reject them with automatic inventory updates.

## Tech Stack

| Layer       | Technology                              |
|-------------|-----------------------------------------|
| Backend     | Java 17, Spring Boot 3.2, Maven         |
| Security    | Spring Security 6, JWT (jjwt 0.11.5)   |
| Database    | H2 In-Memory (JPA / Hibernate)          |
| Frontend    | React 18, Vite 5, React Router v6       |
| HTTP Client | Axios                                   |
| Styling     | Plain CSS (CSS Variables)               |

---

## Authentication Flow

```mermaid
sequenceDiagram
    participant U as User (Browser)
    participant F as Frontend (React)
    participant B as Backend (Spring Boot)
    participant DB as H2 Database

    U->>F: Enter credentials & click Sign In
    F->>B: POST /api/auth/login {username, password}
    B->>DB: SELECT user WHERE username = ?
    DB-->>B: User record (hashed password, role)
    B->>B: BCrypt.verify(password, hash)
    alt Valid credentials
        B->>B: Generate JWT (username + role, 24h expiry)
        B-->>F: 200 { token, username, role }
        F->>F: Store token & user in localStorage
        F-->>U: Redirect to /admin or /employee dashboard
    else Invalid credentials
        B-->>F: 401 { error: "Invalid username or password" }
        F-->>U: Show error message
    end
```

---

## Supply Request Lifecycle

```mermaid
sequenceDiagram
    participant E as Employee
    participant F as Frontend
    participant B as Backend
    participant DB as Database

    E->>F: Fill request form (item, qty, remarks)
    F->>B: POST /api/requests (Bearer token)
    B->>B: Validate JWT, check EMPLOYEE role
    B->>DB: INSERT supply_request (status=PENDING)
    DB-->>B: Saved request
    B-->>F: 200 SupplyRequestResponse
    F-->>E: Show success toast + refresh list

    Note over E,DB: Later — Admin reviews

    E->>F: (Admin logs in)
    F->>B: GET /api/requests (ADMIN token)
    B->>DB: SELECT all requests ORDER BY createdAt DESC
    DB-->>B: All requests list
    B-->>F: 200 [SupplyRequestResponse...]
    F-->>E: Show requests table with Approve/Reject buttons

    alt Admin approves
        E->>F: Click "Approve"
        F->>B: PUT /api/requests/{id}/approve
        B->>DB: SELECT inventory WHERE name = itemName
        DB-->>B: InventoryItem (quantity=N)
        alt Sufficient stock (N >= requested qty)
            B->>DB: UPDATE inventory SET quantity = N - requested
            B->>DB: UPDATE request SET status = APPROVED
            B-->>F: 200 Updated request
            F-->>E: Toast "Request approved and inventory updated"
        else Insufficient stock
            B-->>F: 400 { error: "Insufficient inventory..." }
            F-->>E: Toast error message
        end
    else Admin rejects
        E->>F: Click "Reject", optionally enter reason
        F->>B: PUT /api/requests/{id}/reject { reason }
        B->>DB: UPDATE request SET status=REJECTED, rejectionReason=reason
        DB-->>B: Updated request
        B-->>F: 200 Updated request
        F-->>E: Toast "Request rejected"
    end
```

---

## Data Model

```
users
  id        BIGINT PK
  username  VARCHAR UNIQUE NOT NULL
  password  VARCHAR NOT NULL (BCrypt)
  role      VARCHAR NOT NULL (ADMIN | EMPLOYEE)

inventory_items
  id          BIGINT PK
  name        VARCHAR NOT NULL
  quantity    INT NOT NULL
  description VARCHAR

supply_requests
  id               BIGINT PK
  employee_id      BIGINT FK → users.id
  item_name        VARCHAR NOT NULL
  quantity         INT NOT NULL
  remarks          VARCHAR
  status           VARCHAR NOT NULL (PENDING | APPROVED | REJECTED)
  rejection_reason VARCHAR
  created_at       TIMESTAMP NOT NULL
  updated_at       TIMESTAMP
```

---

## API Endpoints

| Method | Path                          | Role       | Description                            |
|--------|-------------------------------|------------|----------------------------------------|
| POST   | /api/auth/login               | Public     | Authenticate, returns JWT              |
| GET    | /api/inventory                | ADMIN      | List all inventory items               |
| POST   | /api/inventory                | ADMIN      | Add new inventory item                 |
| PUT    | /api/inventory/{id}           | ADMIN      | Update inventory item                  |
| GET    | /api/requests                 | Any Auth   | Admin: all requests; Employee: own     |
| POST   | /api/requests                 | EMPLOYEE   | Submit new supply request              |
| PUT    | /api/requests/{id}/approve    | ADMIN      | Approve request, decrement inventory   |
| PUT    | /api/requests/{id}/reject     | ADMIN      | Reject request with optional reason    |

---

## Project Structure

```
Office-Supply-Management/
├── backend/
│   └── src/main/java/com/officesupply/
│       ├── config/          SecurityConfig, DataSeeder
│       ├── controller/      AuthController, InventoryController, SupplyRequestController
│       ├── dto/             LoginRequest/Response, InventoryItemRequest/Response,
│       │                    SupplyRequestDto/Response, RejectRequest
│       ├── entity/          User, InventoryItem, SupplyRequest
│       ├── exception/       GlobalExceptionHandler
│       ├── repository/      UserRepository, InventoryItemRepository, SupplyRequestRepository
│       ├── security/        JwtUtil, JwtAuthFilter, UserDetailsServiceImpl
│       └── service/         AuthService, InventoryService, SupplyRequestService
└── frontend/
    └── src/
        ├── context/         AuthContext (JWT state, login/logout)
        ├── components/      Navbar, PrivateRoute, StatusBadge
        ├── pages/           LoginPage, EmployeeDashboard, AdminDashboard
        └── services/        api.js (Axios instance + all API calls)
```
