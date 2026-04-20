# Office Supply Management System — Requirements

## 1. Objective

Design and develop a lightweight Office Supply Management System that enables employees to request office supplies and allows administrators to manage inventory and process those requests efficiently.

## 2. Tech Stack

| Layer      | Technology              |
|------------|-------------------------|
| Frontend   | React (Vite)            |
| Backend    | Java 17 + Spring Boot 3 |
| Database   | H2 (dev) / PostgreSQL   |
| Auth       | JWT-based (Spring Security) |
| Build      | Maven                   |

## 3. User Roles

### 3.1 Admin
- View current inventory and stock levels
- Review, approve, or reject supply requests
- Maintain inventory consistency (add/update items)

### 3.2 Employee
- Submit supply requests
- View status of their own submitted requests

## 4. Functional Requirements

### 4.1 Authentication & Authorization
- Login with username and password
- JWT token issued on successful login
- Role-based access: `ADMIN` and `EMPLOYEE`
- Protected API endpoints based on role

### 4.2 Request Submission (Employee)
- Submit a request via form with:
  - **Item Name** (required, selected from existing inventory items)
  - **Quantity** (required, positive integer, ≥ 1)
  - **Remarks** (optional, max 255 characters)
- Each request is timestamped and linked to the logged-in employee
- Default status: `PENDING`

#### Edge Cases
- Quantity ≤ 0 → reject with validation error
- Item not found in inventory → reject
- Duplicate rapid submissions → prevent via frontend debounce
- Quantity exceeds available stock → allow submission (admin decides on approval)

### 4.3 Inventory Management (Admin)
- View all inventory items with current stock levels
- Add new inventory item (name, initial quantity)
- Update stock quantity for existing items

#### Edge Cases
- Duplicate item name → reject with error
- Negative stock value → reject with validation error
- Item name blank or whitespace-only → reject

### 4.4 Request Processing (Admin)
- View all pending requests
- **Approve**: only if sufficient inventory is available → deduct quantity from stock
- **Reject**: optionally provide a rejection reason

#### Edge Cases
- Approve when stock < requested quantity → reject action with "Insufficient stock" error
- Approve an already approved/rejected request → reject action (only `PENDING` requests can be processed)
- Concurrent approval of same item depleting stock → handle with optimistic locking or synchronized stock check

### 4.5 Request Tracking
Each request record contains:

| Field            | Description                          |
|------------------|--------------------------------------|
| Request ID       | Auto-generated unique ID             |
| Employee         | Username / ID of requester           |
| Item Name        | Requested item                       |
| Quantity         | Requested quantity                   |
| Status           | `PENDING` / `APPROVED` / `REJECTED`  |
| Remarks          | Employee remarks (optional)          |
| Rejection Reason | Admin-provided reason (if rejected)  |
| Created At       | Timestamp of submission              |
| Updated At       | Timestamp of last status change      |

- Employee sees only their own requests
- Admin sees all requests

### 4.6 User Interface

#### Employee View
- Dashboard showing their request history with status
- "New Request" form
- Filter requests by status

#### Admin View
- Inventory list with stock levels
- Pending requests queue with approve/reject actions
- Request history (all employees)
- Filter/search by status, employee, or item name

## 5. Non-Functional Requirements

- **Responsive**: usable on desktop and mobile
- **Validation**: enforced on both frontend and backend
  - No negative or zero quantities
  - Required fields must be filled
  - Max length on text fields
- **Error Handling**: meaningful error messages returned from API
- **Lightweight**: minimal dependencies, fast startup

## 6. API Endpoints (Overview)

| Method | Endpoint                     | Role     | Description                  |
|--------|------------------------------|----------|------------------------------|
| POST   | `/api/auth/login`            | Public   | Login, returns JWT           |
| POST   | `/api/auth/register`         | Public   | Register new employee        |
| GET    | `/api/inventory`             | Admin    | List all inventory items     |
| POST   | `/api/inventory`             | Admin    | Add new inventory item       |
| PUT    | `/api/inventory/{id}`        | Admin    | Update stock quantity        |
| POST   | `/api/requests`              | Employee | Submit a new request         |
| GET    | `/api/requests`              | Both     | Get requests (filtered by role) |
| PUT    | `/api/requests/{id}/approve` | Admin    | Approve a pending request    |
| PUT    | `/api/requests/{id}/reject`  | Admin    | Reject a pending request     |

## 7. High-Level Flow

```
1. Employee logs in → receives JWT
2. Employee submits request → status = PENDING
3. Admin logs in → sees pending requests
4. Admin reviews request:
   a. Stock available → Approve → inventory deducted, status = APPROVED
   b. Stock insufficient or other reason → Reject (with reason), status = REJECTED
5. Employee views updated request status
```

## 8. Optional Enhancements (Future Scope)

- Low-stock alerts for admin
- Dashboard with stats (pending count, approved vs rejected ratio)
- Pagination for request history
- Email/notification on request status change
- Search and filter by date range

