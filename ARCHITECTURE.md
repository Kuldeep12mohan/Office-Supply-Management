# Office Supply Management System - Architecture Document

## System Architecture Overview

### High-Level Architecture Diagram
```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENT TIER (Frontend)                     │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  React.js Application (Port 3000/5173)               │   │
│  │  - Login Page                                        │   │
│  │  - Employee Dashboard & Request Form                │   │
│  │  - Admin Dashboard & Management Pages               │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ↓↑
                    HTTP/REST/JSON
                            ↓↑
┌─────────────────────────────────────────────────────────────┐
│              APPLICATION TIER (Backend)                       │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Spring Boot REST API (Port 8080)                    │   │
│  │  ┌─────────────────────────────────────────────┐    │   │
│  │  │ Controllers (Request Handlers)               │    │   │
│  │  │ - AuthController                            │    │   │
│  │  │ - SupplyRequestController                   │    │   │
│  │  │ - InventoryController                       │    │   │
│  │  │ - UserController                            │    │   │
│  │  │ - AuditLogController                        │    │   │
│  │  └─────────────────────────────────────────────┘    │   │
│  │          ↓                                           │   │
│  │  ┌─────────────────────────────────────────────┐    │   │
│  │  │ Services (Business Logic)                    │    │   │
│  │  │ - UserService                               │    │   │
│  │  │ - SupplyRequestService                      │    │   │
│  │  │ - InventoryService                          │    │   │
│  │  │ - AuditLogService                           │    │   │
│  │  └─────────────────────────────────────────────┘    │   │
│  │          ↓                                           │   │
│  │  ┌─────────────────────────────────────────────┐    │   │
│  │  │ Data Access Layer (Repositories)            │    │   │
│  │  │ - UserRepository (JpaRepository)            │    │   │
│  │  │ - SupplyRequestRepository                   │    │   │
│  │  │ - InventoryRepository                       │    │   │
│  │  │ - AuditLogRepository                        │    │   │
│  │  └─────────────────────────────────────────────┘    │   │
│  │  ┌─────────────────────────────────────────────┐    │   │
│  │  │ Security & Config                           │    │   │
│  │  │ - SecurityConfig (Spring Security)          │    │   │
│  │  │ - CustomUserDetailsService                  │    │   │
│  │  │ - PasswordEncoder (BCrypt)                  │    │   │
│  │  └─────────────────────────────────────────────┘    │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ↓↑
                    JDBC/Connection Pool
                            ↓↑
┌─────────────────────────────────────────────────────────────┐
│             DATA TIER (Database)                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  MySQL Database (Port 3306)                          │   │
│  │  - users table                                       │   │
│  │  - supply_requests table                            │   │
│  │  - inventory table                                  │   │
│  │  - audit_logs table                                 │   │
│  │  - Database Migrations (Flyway)                     │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### Technology Stack
- **Backend**: Java 17, Spring Boot 3.1.5
- **Frontend**: React 18.2, Vite, Tailwind CSS
- **Database**: MySQL 8.0
- **ORM**: Hibernate JPA
- **Security**: Spring Security with BCrypt password encoding
- **Build**: Maven
- **Testing**: JUnit 5, Mockito, React Testing Library
- **Code Coverage**: JaCoCo (Java), Jest/Vitest (React)
- **Migration**: Flyway

---

## Database Schema & Entity Relationships

### Database Diagram
```
┌─────────────────┐
│     USERS       │
├─────────────────┤
│ id (PK)         │
│ username (U)    │
│ email (U)       │
│ password        │
│ full_name       │
│ role            │
│ enabled         │
│ created_at      │
│ updated_at      │
└─────────────────┘
        ▲
        │ (1..*)  Admin/Employee
        │
        ├ (1) requested_by
        │                    ┌─────────────────────────┐
        │                    │  SUPPLY_REQUESTS        │
        └────────────────────┤─────────────────────────┤
        ├ (1) approved_by    │ id (PK)                 │
        │                    │ item_name               │
        └────────────────────┤ quantity                │
                             │ remarks                 │
                             │ status                  │
                             │ requested_by (FK)       │
                             │ approved_by (FK)        │
                             │ approval_reason         │
                             │ rejection_reason        │
                             │ created_at              │
                             │ approved_at             │
                             │ rejected_at             │
                             └─────────────────────────┘

┌─────────────────┐
│   INVENTORY     │
├─────────────────┤
│ id (PK)         │
│ item_name (U)   │◄─── References
│ quantity        │     item_name
│ reorder_level   │     in SUPPLY_REQUESTS
│ last_updated_by │ (FK to USERS)
│ created_at      │
│ updated_at      │
└─────────────────┘
        ▲
        │
        ├ (1) action_by
        │                    ┌──────────────────┐
        └────────────────────┤  AUDIT_LOGS      │
                             ├──────────────────┤
                             │ id (PK)          │
                             │ action           │
                             │ action_by (FK)   │
                             │ item_id          │
                             │ details          │
                             │ created_at       │
                             └──────────────────┘
```

### Entity Specifications

#### User Entity
```
TABLE: users
┌─────────────────────────────────────────────────────────┐
│ Column          │ Type         │ Constraints            │
├─────────────────┼──────────────┼────────────────────────┤
│ id              │ BIGINT       │ PRIMARY KEY, AUTO_INC  │
│ username        │ VARCHAR(50)  │ UNIQUE, NOT NULL       │
│ email           │ VARCHAR(100) │ UNIQUE, NOT NULL       │
│ password        │ VARCHAR(255) │ NOT NULL (HASHED)      │
│ full_name       │ VARCHAR(100) │ NOT NULL               │
│ role            │ VARCHAR(20)  │ NOT NULL (ADMIN/EMP)   │
│ enabled         │ BOOLEAN      │ DEFAULT TRUE           │
│ created_at      │ TIMESTAMP    │ DEFAULT NOW()          │
│ updated_at      │ TIMESTAMP    │ DEFAULT NOW()          │
└─────────────────────────────────────────────────────────┘
```

#### SupplyRequest Entity
```
TABLE: supply_requests
┌──────────────────────────────────────────────────────────┐
│ Column           │ Type         │ Constraints             │
├──────────────────┼──────────────┼─────────────────────────┤
│ id               │ BIGINT       │ PRIMARY KEY, AUTO_INC   │
│ item_name        │ VARCHAR(100) │ NOT NULL                │
│ quantity         │ INT          │ NOT NULL, > 0           │
│ remarks          │ TEXT         │ Optional                │
│ status           │ VARCHAR(20)  │ NOT NULL (ENUM)         │
│ requested_by     │ BIGINT (FK)  │ References users(id)    │
│ approved_by      │ BIGINT (FK)  │ References users(id)    │
│ approval_reason  │ TEXT         │ Optional                │
│ rejection_reason │ TEXT         │ Optional                │
│ created_at       │ TIMESTAMP    │ DEFAULT NOW()           │
│ approved_at      │ TIMESTAMP    │ NULL by default         │
│ rejected_at      │ TIMESTAMP    │ NULL by default         │
└──────────────────────────────────────────────────────────┘
```

#### Inventory Entity
```
TABLE: inventory
┌─────────────────────────────────────────────────────────┐
│ Column          │ Type         │ Constraints            │
├─────────────────┼──────────────┼────────────────────────┤
│ id              │ BIGINT       │ PRIMARY KEY, AUTO_INC  │
│ item_name       │ VARCHAR(100) │ UNIQUE, NOT NULL       │
│ quantity        │ INT          │ NOT NULL, >= 0         │
│ reorder_level   │ INT          │ NOT NULL, > 0          │
│ last_updated_by │ BIGINT (FK)  │ References users(id)   │
│ created_at      │ TIMESTAMP    │ DEFAULT NOW()          │
│ updated_at      │ TIMESTAMP    │ DEFAULT NOW()          │
└─────────────────────────────────────────────────────────┘
```

---

## API Contract & Endpoints

### Base URL
```
http://localhost:8080/api
```

### Authentication Endpoints

#### Login
```
POST /auth/login
Content-Type: application/json

Request:
{
  "username": "string",
  "password": "string"
}

Response: 200 OK
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": 1,
    "username": "admin",
    "fullName": "Admin User",
    "role": "Admin"
  },
  "timestamp": "2024-01-15T10:30:00"
}

Errors:
- 401 Unauthorized: Invalid credentials
```

#### Logout
```
POST /auth/logout
Authorization: Session Cookie

Response: 200 OK
{
  "success": true,
  "message": "Logged out successfully"
}
```

#### Get Current User
```
GET /auth/me
Authorization: Session Cookie

Response: 200 OK
{
  "success": true,
  "message": "User retrieved",
  "data": { ...user data... }
}
```

### Supply Request Endpoints

#### Create Supply Request (Employee)
```
POST /requests
Authorization: Session (EMPLOYEE)
Content-Type: application/json

Request:
{
  "itemName": "Printer Paper (A4)",
  "quantity": 10,
  "remarks": "For office use"
}

Response: 201 Created
{
  "success": true,
  "message": "Request created successfully",
  "data": {
    "id": 1,
    "itemName": "Printer Paper (A4)",
    "quantity": 10,
    "status": "PENDING",
    "createdAt": "2024-01-15T10:30:00"
  }
}

Errors:
- 400 Bad Request: Validation failed
- 401 Unauthorized: Not authenticated
```

#### Get My Requests (Employee)
```
GET /requests/my-requests
Authorization: Session (EMPLOYEE)

Response: 200 OK
{
  "success": true,
  "message": "Requests retrieved",
  "data": [
    { ...request1... },
    { ...request2... }
  ]
}
```

#### Get All Requests (Admin)
```
GET /requests
Authorization: Session (ADMIN)

Response: 200 OK
{
  "success": true,
  "message": "All requests retrieved",
  "data": [ ...all requests... ]
}
```

#### Get Pending Requests (Admin)
```
GET /requests/pending
Authorization: Session (ADMIN)

Response: 200 OK
{
  "success": true,
  "data": [ ...pending requests... ]
}
```

#### Get Request Details
```
GET /requests/{id}
Authorization: Session

Response: 200 OK
{
  "success": true,
  "data": { ...request details... }
}

Errors:
- 404 Not Found: Request not found
```

#### Approve Request (Admin Only)
```
PUT /requests/{id}/approve
Authorization: Session (ADMIN)
Content-Type: application/x-www-form-urlencoded

Query Parameters:
- reason (required): Approval reason

Response: 200 OK
{
  "success": true,
  "message": "Request approved successfully",
  "data": {
    "id": 1,
    "status": "APPROVED",
    "approvedBy": "Admin User",
    "approvalReason": "Stock available"
  }
}

Errors:
- 400 Bad Request: Insufficient inventory
- 403 Forbidden: Not authorized
```

#### Reject Request (Admin Only)
```
PUT /requests/{id}/reject
Authorization: Session (ADMIN)

Query Parameters:
- reason (optional): Rejection reason

Response: 200 OK
{
  "success": true,
  "message": "Request rejected successfully",
  "data": {
    "id": 1,
    "status": "REJECTED",
    "rejectionReason": "Item out of stock"
  }
}
```

### Inventory Endpoints

#### Get All Inventory (Admin)
```
GET /inventory
Authorization: Session (ADMIN)

Response: 200 OK
{
  "success": true,
  "data": [
    {
      "id": 1,
      "itemName": "Printer Paper",
      "quantity": 500,
      "reorderLevel": 50
    },
    ...
  ]
}
```

#### Get Inventory Item (Admin)
```
GET /inventory/{id}
Authorization: Session (ADMIN)

Response: 200 OK
{ ...inventory item... }
```

#### Create Inventory Item (Admin)
```
POST /inventory
Authorization: Session (ADMIN)
Content-Type: application/json

Request:
{
  "itemName": "Printer Paper (A4)",
  "quantity": 500,
  "reorderLevel": 50
}

Response: 201 Created
{ ...inventory item... }
```

#### Update Inventory (Admin)
```
PUT /inventory/{id}
Authorization: Session (ADMIN)
Content-Type: application/json

Request:
{
  "quantity": 600,
  "reorderLevel": 60
}

Response: 200 OK
{ ...updated inventory item... }
```

#### Get Low Stock Items (Admin)
```
GET /inventory/low-stock
Authorization: Session (ADMIN)

Response: 200 OK
{
  "success": true,
  "data": [ ...items with quantity <= reorderLevel... ]
}
```

### Audit Logs Endpoint

#### Get Audit Logs (Admin)
```
GET /logs
Authorization: Session (ADMIN)

Response: 200 OK
{
  "success": true,
  "data": [
    {
      "id": 1,
      "action": "REQUEST_CREATED",
      "actionBy": null,
      "details": "{...}",
      "createdAt": "2024-01-15T10:30:00"
    },
    ...
  ]
}
```

---

## Frontend Component Tree

```
App.jsx
├── Providers
│   ├── AuthContext Provider
│   └── Router
│
├── Public Routes
│   ├── LoginPage
│   │   ├── LoginForm
│   │   └── ErrorAlert
│   └── UnauthorizedPage
│
├── Protected Routes
│   ├── Layout
│   │   ├── Header
│   │   │   ├── Logo
│   │   │   ├── Navigation
│   │   │   ├── UserMenu
│   │   │   └── LogoutButton
│   │   │
│   │   ├── Sidebar
│   │   │   └── Navigation Links (role-based)
│   │   │
│   │   └── MainContent (Outlet)
│   │
│   ├── Employee Routes
│   │   ├── EmployeeDashboard
│   │   │   ├── WelcomeCard
│   │   │   ├── StatsCards
│   │   │   │   ├── TotalRequests
│   │   │   │   ├── PendingRequests
│   │   │   │   ├── ApprovedRequests
│   │   │   │   └── RejectedRequests
│   │   │   └── RecentRequests
│   │   │
│   │   ├── SubmitRequestPage
│   │   │   ├── RequestForm
│   │   │   │   ├── ItemNameInput
│   │   │   │   ├── QuantityInput
│   │   │   │   ├── RemarksInput
│   │   │   │   ├── SubmitButton
│   │   │   │   └── ValidationErrors
│   │   │   └── SuccessMessage
│   │   │
│   │   ├── MyRequestsPage
│   │   │   ├── FilterBar
│   │   │   ├── SearchBox
│   │   │   ├── RequestsTable
│   │   │   │   ├── RequestRow
│   │   │   │   └── StatusBadge
│   │   │   └── Pagination
│   │   │
│   │   └── RequestDetailsPage
│   │       ├── RequestHeader
│   │       ├── RequestInfo
│   │       └── TimelineHistory
│   │
│   └── Admin Routes
│       ├── AdminDashboard
│       │   ├── StatsOverview
│       │   ├── InventorySummary
│       │   ├── RecentActivities
│       │   └── QuickActions
│       │
│       ├── PendingRequestsPage
│       │   ├── RequestsTable
│       │   │   ├── RequestRow
│       │   │   └── ActionButtons
│       │   │       ├── ApproveButton
│       │   │       └── RejectButton
│       │   ├── ApprovalModal
│       │   │   ├── ReasonInput
│       │   │   └── ConfirmButton
│       │   └── RejectionModal
│       │       ├── RejectionReasonInput
│       │       └── ConfirmButton
│       │
│       ├── InventoryManagementPage
│       │   ├── InventoryTable
│       │   │   ├── ItemRow
│       │   │   │   ├── ItemDetail
│       │   │   │   ├── QuantityDisplay
│       │   │   │   ├── StatusIndicator
│       │   │   │   └── EditButton
│       │   │   └── Search
│       │   ├── AddItemModal
│       │   │   ├── ItemNameInput
│       │   │   ├── QuantityInput
│       │   │   ├── ReorderLevelInput
│       │   │   └── SaveButton
│       │   └── EditItemModal
│       │       ├── QuantityInput
│       │       ├── ReorderLevelInput
│       │       └── SaveButton
│       │
│       ├── RequestHistoryPage
│       │   ├── FilterOptions
│       │   ├── HistoryTable
│       │   │   ├── HistoryRow
│       │   │   └── DetailsLink
│       │   └── Pagination
│       │
│       └── UserManagementPage
│           ├── UsersTable
│           │   ├── UserRow
│           │   └── DisableButton
│           ├── AddUserModal
│           │   ├── UsernameInput
│           │   ├── EmailInput
│           │   ├── PasswordInput
│           │   ├── RoleSelect
│           │   └── CreateButton
│           └── EditUserModal
│
└── Common Components
    ├── NotFound (404)
    ├── ErrorBoundary
    ├── LoadingSpinner
    ├── Toast Notifications
    └── Modals
```

---

## Security Architecture

### Authentication & Authorization
- **Method**: Session-based with Spring Security
- **Password Encoding**: BCrypt (strength factor: 10)
- **Session Timeout**: 30 minutes of inactivity
- **CSRF Protection**: Enabled by default

### Authorization Rules
```
Public Endpoints:
  - /auth/login
  - /auth/logout (authenticated users only)

Employee Access (ROLE_EMPLOYEE):
  - POST /requests - Create supply request
  - GET /requests/my-requests - View own requests
  - GET /requests/{id} - View request details

Admin Access (ROLE_ADMIN):
  - GET /requests - View all requests
  - GET /requests/pending - View pending requests
  - PUT /requests/{id}/approve - Approve request
  - PUT /requests/{id}/reject - Reject request
  - GET /inventory - View inventory
  - POST /inventory - Create inventory
  - PUT /inventory/{id} - Update inventory
  - GET /logs - View audit logs
  - GET /users - View users
```

### Data Protection
- All passwords hashed with BCrypt before storage
- No sensitive data in logs
- SQL injection prevention via parameterized queries
- XSS protection via React's built-in sanitization

---

## Deployment Architecture

### Development Environment
```
Local Machine
├── Frontend (Vite Dev Server) - localhost:5173/3000
└── Backend (Spring Boot) - localhost:8080
    └── MySQL Database - localhost:3306
```

### Production Environment (Ready for)
```
Docker/Cloud Deployment
├── Docker Container: Frontend (Nginx)
├── Docker Container: Backend (Spring Boot JAR)
└── Managed Database: MySQL (Cloud)
```

### Build & Deployment Pipeline
1. **Build Backend**: `mvn clean package`
2. **Build Frontend**: `npm run build`
3. **Docker Images**: Create Dockerfile for containerization
4. **Deploy to**: AWS ECS, Azure Container Instances, etc.

---

## Performance Considerations

### Database Optimization
- Indexed fields: username, email, role, status, created_at
- Connection pool: 10 connections max
- Query timeouts: 30 seconds

### Caching Strategy
- Sessions: Server-side (30 min TTL)
- Static assets: Browser cache on CDN

### Scalability
- Stateless API design
- Database connection pooling
- Load balancer ready (multi-instance support)

---

## Error Handling

### Global Exception Handler
```
ResourceNotFoundException (404)
↓
InsufficientInventoryException (400)
↓
UnauthorizedAccessException (403)
↓
ValidationException (400)
↓
General Exception (500)
```

### Response Format
```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "ISO-8601 timestamp"
}
```

---

## Success Criteria Checklist
- [x] Backend structure initialized
- [x] Database schema created
- [x] JPA entities and repositories implemented
- [x] Services with business logic developed
- [x] REST controllers with endpoints created
- [x] Spring Security configuration complete
- [x] Flyway migrations setup
- [x] Exception handling implemented
- [ ] Frontend components created (Next phase)
- [ ] Integration tests written (Next phase)
- [ ] Unit tests written (Next phase)
- [ ] Code coverage reports generated (Next phase)
- [ ] API documentation complete
