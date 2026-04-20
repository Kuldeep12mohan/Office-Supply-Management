# Office Supply Management System - Project Plan

## Project Overview
A web-based inventory management system for office supplies where employees can submit supply requests and admins can manage inventory and approve/reject requests. The system maintains a complete audit trail of all transactions.

---

## Tech Stack (Justified)

### Backend: Java Spring Boot ✅
- Industry standard for enterprise applications
- Excellent for building REST APIs and microservices
- Rich ecosystem with Spring Security, JPA, and cloud support

### Frontend: React.js + Tailwind CSS
- **Justification**: Modern SPA with excellent component reusability. React's virtual DOM ensures responsive UI. Tailwind CSS provides rapid styling for simple, clean admin dashboards. Perfect for this project's moderate complexity with role-based UI components.

### Database: MySQL
- **Justification**: Relational database ideal for structured data (inventory, users, requests). MySQL is lightweight, reliable, and free. Easy integration with Spring JPA. Supports complex queries for audit trails and request history.

### Authentication: Session-based with Spring Security
- **Justification**: Simpler than JWT for this internal system. Spring Security provides built-in session management, CSRF protection, and easy role-based access control (Admin/Employee).

### Additional Tools:
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, React Testing Library
- **HTTP Client**: Axios (Frontend)
- **UI Components**: React Hooks, Context API for state management
- **CSS**: Tailwind CSS + Responsive Design
- **Database Migration**: Flyway
- **Logging**: Log4j2

---

## Modules & Features

### Module 1: User Management
- User registration (admin-controlled)
- User login/logout
- Role assignment (Admin/Employee)
- User profile management

### Module 2: Supply Request Management
- Create supply requests (Employee)
- View request status (Employee)
- List all requests (Admin)
- Approve requests (Admin)
- Reject requests with reason (Admin)
- Request history and audit trail

### Module 3: Inventory Management
- View inventory (Admin only)
- Update inventory (auto-update on approval)
- Track inventory levels
- Low stock alerts

### Module 4: Dashboard & Analytics
- Employee dashboard (request status)
- Admin dashboard (pending requests, inventory overview)
- Request history logs

---

## User Stories

### As an Employee
1. **Submit Supply Request**
   - I want to submit a request for office supplies with item name, quantity, and optional remarks
   - So that I can get the supplies I need for my work

2. **Track Request Status**
   - I want to view the status of my submitted requests
   - So that I know if they are pending, approved, or rejected

3. **View Request History**
   - I want to see a history of all my requests
   - So that I can track what I've requested in the past

### As an Admin
1. **View Inventory**
   - I want to see current inventory levels of all office supplies
   - So that I can make approval decisions based on availability

2. **Review Requests**
   - I want to view all pending supply requests from employees
   - So that I can approve or reject them appropriately

3. **Approve Requests**
   - I want to approve supply requests and automatically deduct from inventory
   - So that employees get their approved supplies

4. **Reject Requests**
   - I want to reject supply requests with an optional reason
   - So that employees understand why their request was denied

5. **Manage Inventory**
   - I want to manually add/update inventory levels
   - So that the system reflects current stock

---

## Database Entities & Relationships

### Entity: User
```
id (PK)
username (UNIQUE)
email (UNIQUE)
password (HASHED)
fullName
role (ENUM: ADMIN, EMPLOYEE)
createdAt
updatedAt
```

### Entity: SupplyRequest
```
id (PK)
itemName
quantity
remarks
status (ENUM: PENDING, APPROVED, REJECTED)
requestedBy (FK -> User)
approvedBy (FK -> User, nullable)
approvalReason
rejectionReason
createdAt
approvedAt (nullable)
rejectedAt (nullable)
```

### Entity: Inventory
```
id (PK)
itemName (UNIQUE)
quantity
reorderLevel
lastUpdatedBy (FK -> User)
updatedAt
createdAt
```

### Entity: AuditLog
```
id (PK)
action (ENUM: REQUEST_CREATED, REQUEST_APPROVED, REQUEST_REJECTED, INVENTORY_UPDATED)
actionBy (FK -> User)
itemId
details (JSON)
createdAt
```

### Relationships:
- User (1) --> (M) SupplyRequest (requestedBy)
- User (1) --> (M) SupplyRequest (approvedBy)
- User (1) --> (M) AuditLog
- SupplyRequest (M) --> (1) Inventory (itemName reference)

---

## API Endpoints (Method, URL, Access Role)

### Authentication
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/login` | PUBLIC | User login |
| POST | `/api/auth/logout` | AUTHENTICATED | User logout |
| GET | `/api/auth/me` | AUTHENTICATED | Get current user |

### Supply Requests
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/api/requests` | EMPLOYEE | Submit supply request |
| GET | `/api/requests/my-requests` | EMPLOYEE | Get own requests |
| GET | `/api/requests` | ADMIN | Get all requests |
| GET | `/api/requests/{id}` | AUTHENTICATED | Get request details |
| PUT | `/api/requests/{id}/approve` | ADMIN | Approve request |
| PUT | `/api/requests/{id}/reject` | ADMIN | Reject request |
| GET | `/api/requests/history` | ADMIN | Get request history |

### Inventory
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | `/api/inventory` | ADMIN | View all inventory |
| GET | `/api/inventory/{id}` | ADMIN | Get specific item inventory |
| PUT | `/api/inventory/{id}` | ADMIN | Update inventory |
| POST | `/api/inventory` | ADMIN | Add new inventory item |

### Audit Logs
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | `/api/logs` | ADMIN | Get audit logs |

### Users (Admin Management)
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/api/users` | ADMIN | Create user |
| GET | `/api/users` | ADMIN | List all users |
| GET | `/api/users/{id}` | ADMIN | Get user details |
| PUT | `/api/users/{id}` | ADMIN | Update user |

---

## UI Pages/Screens

### Public Pages
1. **Login Page** - Username/password login form
2. **Unauthorized Page** - 403 error page

### Employee Pages
1. **Employee Dashboard**
   - Welcome message
   - Quick stats (total requests, pending, approved, rejected)
   - List of own requests with status

2. **Submit Request Page**
   - Form with item name, quantity, remarks
   - Submit button
   - Success/error message

3. **Request Details Page**
   - Full request information
   - Current status with timestamp
   - Approval/rejection reason (if applicable)

4. **My Requests Page**
   - Filterable list of all employee's requests
   - Search functionality
   - Status badges (Pending/Approved/Rejected)

### Admin Pages
1. **Admin Dashboard**
   - Stats (total requests, pending, approved, rejected)
   - Inventory overview
   - Recent activities
   - Quick actions

2. **Pending Requests Page**
   - Table of all pending requests
   - Request details (item, quantity, requester, date)
   - Approve/Reject buttons with reason field
   - Filter and search

3. **Inventory Management Page**
   - Search inventory items
   - Add new inventory item
   - Edit existing item quantities
   - Low stock alerts

4. **Request History Page**
   - Complete history with filters
   - Status breakdown
   - Audit trail view

5. **User Management Page**
   - List all users
   - Add new users
   - Assign roles
   - Edit user details

### Common Pages
1. **Header/Navigation** - Logout, profile link, role indicator
2. **404 Page** - Not found error
3. **Error Page** - General error handling

---

## Task Breakdown (Priority Ordered)

### Sprint 1: Foundation & Setup (CRITICAL)
1. ✓ Analyze requirements and create plan
2. Initialize Spring Boot backend project with Maven
3. Initialize React.js frontend project
4. Configure Spring Security and authentication
5. Create database schema (user, inventory, request, audit)
6. Setup MySQL database
7. Create User entity and service
8. Implement login/logout API endpoints

### Sprint 2: Core Feature - Supply Requests (HIGH)
9. Create SupplyRequest entity and repository
10. Implement "Submit Request" API (Employee)
11. Implement "View My Requests" API (Employee)
12. Implement "View All Requests" API (Admin)
13. Implement "Approve Request" API (Admin)
14. Implement "Reject Request" API (Admin)
15. Create Request DTOs and validators

### Sprint 3: Inventory Management (HIGH)
16. Create Inventory entity and repository
17. Implement "View Inventory" API (Admin)
18. Implement "Update Inventory" API (Admin)
19. Auto-deduct inventory on request approval
20. Implement low stock alerts

### Sprint 4: Frontend - Core Pages (HIGH)
21. Setup React project with routing
22. Create Login page and integration
23. Create Employee Dashboard
24. Create Submit Request form
25. Create My Requests page
26. Create Request Details page
27. Create Admin Dashboard
28. Create Pending Requests page (Admin)
29. Create Inventory Management page

### Sprint 5: Frontend - Additional Pages (MEDIUM)
30. Create Request History page
31. Create User Management page (Admin)
32. Create navigation and layout components
33. Add error handling and notifications
34. Add filters and search functionality

### Sprint 6: Backend Refinements (MEDIUM)
35. Implement audit logging
36. Add input validation and sanitization
37. Implement pagination for list endpoints
38. Add search and filter support
39. Add exception handling and error responses

### Sprint 7: Testing & Quality (CRITICAL)
40. Write unit tests for backend services
41. Write integration tests for APIs
42. Write unit tests for React components
43. Write integration tests for user flows
44. Generate test coverage reports
45. Write test documentation
46. Performance testing and optimization

### Sprint 8: Deployment & Finalization (CRITICAL)
47. Code review and fixes
48. Prepare deployment artifacts
49. Generate test and coverage reports
50. Commit to GitHub

---

## Edge Cases & Constraints

### Business Logic Edge Cases
1. Approve request with zero inventory → Reject (insufficient stock)
2. Multiple requests for same item → Queue management
3. User approval on their own request → Not allowed (another admin required)
4. Delete user who has pending requests → Archive only, no delete
5. Concurrent approval of same request → Last update wins + audit trail
6. Negative quantity requests → Validation error
7. Request rejection without reason → Allow, but log as "No reason provided"

### Technical Constraints
1. Session timeout after 30 minutes of inactivity
2. Request payload size limit: 5MB
3. File upload not supported in this phase
4. Scale: Up to 1000 users, 10,000 requests testing
5. Database connection pool: 10 connections
6. API response timeout: 30 seconds

### Security & Compliance
1. All passwords must be hashed (BCrypt)
2. No sensitive data in logs
3. CSRF protection on all state-changing requests
4. SQL injection prevention via parameterized queries
5. XSS protection via React's built-in sanitization
6. Role-based access control strictly enforced
7. Audit trail for all critical actions

---

## Testing Strategy

### Unit Testing
- Service layer tests (all business logic)
- Controller tests (endpoint validation)
- Utility function tests
- Frontend component unit tests

### Integration Testing
- API endpoint integration tests
- Database transaction tests
- Authentication flow tests
- End-to-end workflow tests

### Test Coverage Goals
- Backend: Minimum 80% code coverage
- Frontend: Minimum 75% component coverage

### Test Reports Required
1. Code coverage report (JaCoCo for Java, Jest for React)
2. Test execution report (all test cases)
3. Test documentation (test cases, scenarios)
4. Performance metrics

---

## Sequence Diagram (User Flows)

### Employee Request Flow
```
Employee → Frontend → Backend → Database
   │         │          │         │
   ├─Submit Request──────→ API    │
   │                      │       │
   │                      ├─Save Request──→ DB
   │                      │       ←─
   │         ◄────Response─┤
   ├─View Status (Polling)─→ API
   │                      ├─Fetch Status──→ DB
   │                      │       ←─
   │         ◄────Status──┤
```

### Admin Approval Flow
```
Admin → Frontend → Backend → Database → Employee
  │       │         │         │         │
  ├─View Pending────→ API      │        │
  │                 ├─Fetch───→ DB      │
  │                 │         ←─        │
  │      ◄────List─┤                   │
  │                                      │
  ├─Approve Request─→ API               │
  │                 ├─Update───→ DB      │
  │                 ├─Deduct Inventory   │
  │                 └─Create Log────→ DB │
  │                 │         ←─        │
  │      ◄────Success─┤                 │
  │                 └─Send Notification→ Employee
```

---

## Success Criteria Checklist
- [ ] PLAN.md completed and reviewed
- [ ] Ready for Architect phase (ARCHITECTURE.md)
- [ ] All requirements mapped to user stories
- [ ] Tech stack selected and justified
- [ ] Database schema designed
- [ ] API endpoints defined
- [ ] UI pages planned
- [ ] Test strategy defined
