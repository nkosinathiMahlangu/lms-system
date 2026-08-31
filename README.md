# Leave Management System (LMS)

A full-stack Leave Management System built with Spring Boot and React. Employees can apply for leave and track their requests, while admins manage users, leave types, and approve or reject applications.

## Live Demo

**App:** https://lms-system-topaz.vercel.app

> The backend is hosted on a free tier — the first request after a period of inactivity may take up to ~30 seconds to wake up. After that it's fast.

### Demo Credentials

Password for **all** demo accounts: `Demo@1234`

| Role | Username |
|------|----------|
| Admin | `admin` |
| Admin | `sarah.admin` |
| Employee | `john.doe` |
| Employee | `thabo.m` |
| Employee | `lerato.k` |

---

## Tech Stack

**Backend**
- Java 21 + Spring Boot 4
- Spring Security + JWT (stateless authentication)
- Spring Data JPA + Hibernate
- PostgreSQL
- Maven

**Frontend**
- React + Vite
- Axios (API communication)

---

## Features

### Admin
- Create and delete employee accounts
- View all users
- Approve or reject leave requests
- View all leave requests (filterable by status)
- Rename and delete leave types

### Employee
- Apply for leave
- View own leave history
- Cancel a pending leave request
- View remaining leave balance per leave type

### Auth
- JWT-based login (1-hour token)
- Forgot password via OTP email (3-minute expiry)
- OTP-based password reset

---

## Project Structure

```
lms/
├── LMS-System/     # Spring Boot backend
└── LMS-Web/        # React frontend
```

---

## Getting Started

### Prerequisites
- Java 21
- Maven
- PostgreSQL
- Node.js + npm

### 1. Database Setup

Create a PostgreSQL database named `lms`, then run the app once to let Hibernate create the tables. After that seed the data below.

All demo user passwords are `Demo@1234`.

```sql
-- ============================================================
-- Roles
-- ============================================================
INSERT INTO roles (name) VALUES ('ADMIN'), ('EMPLOYEE');

-- ============================================================
-- Leave Types
-- ============================================================
INSERT INTO leave_types (name, default_days) VALUES
  ('Annual Leave', 21),
  ('Sick Leave', 10),
  ('Family Responsibility Leave', 3);

-- ============================================================
-- Users  (password for all = Demo@1234)
-- ============================================================
INSERT INTO users (first_name, last_name, email, username, password, enabled) VALUES
  ('Nkosinathi', 'Mahlangu', 'admin@lms.com',      'admin',       '$2a$10$jSHEGODDWd5vjGIWn30ACeWQQ6zHAW6aP2Kgl5bDtbxAQyvmpv9xC', true),
  ('Sarah',      'Ndlovu',   'sarah@lms.com',      'sarah.admin', '$2a$10$jSHEGODDWd5vjGIWn30ACeWQQ6zHAW6aP2Kgl5bDtbxAQyvmpv9xC', true),
  ('John',       'Doe',      'john@lms.com',       'john.doe',    '$2a$10$jSHEGODDWd5vjGIWn30ACeWQQ6zHAW6aP2Kgl5bDtbxAQyvmpv9xC', true),
  ('Thabo',      'Mokoena',  'thabo@lms.com',      'thabo.m',     '$2a$10$jSHEGODDWd5vjGIWn30ACeWQQ6zHAW6aP2Kgl5bDtbxAQyvmpv9xC', true),
  ('Lerato',     'Khumalo',  'lerato@lms.com',     'lerato.k',    '$2a$10$jSHEGODDWd5vjGIWn30ACeWQQ6zHAW6aP2Kgl5bDtbxAQyvmpv9xC', true);

-- ============================================================
-- Assign roles
-- ============================================================
-- Admins
INSERT INTO user_roles (user_id, role_id)
SELECT u.user_id, (SELECT id FROM roles WHERE name = 'ADMIN')
FROM users u WHERE u.username IN ('admin', 'sarah.admin');

-- Employees
INSERT INTO user_roles (user_id, role_id)
SELECT u.user_id, (SELECT id FROM roles WHERE name = 'EMPLOYEE')
FROM users u WHERE u.username IN ('john.doe', 'thabo.m', 'lerato.k');

-- ============================================================
-- Seed leave balances for the 3 employees (one row per leave type)
-- ============================================================
INSERT INTO leave_balances (user_id, leave_type_id, remaining_days)
SELECT u.user_id, lt.id, lt.default_days
FROM users u
CROSS JOIN leave_types lt
WHERE u.username IN ('john.doe', 'thabo.m', 'lerato.k');
```

> **Note on passwords:** The hash above is the BCrypt hash of `Demo@1234`. Because BCrypt is one-way, the same plaintext produces this exact hash for every user, which is why all demo accounts share it.

### 2. Backend Configuration

Copy and fill in your local secrets:

```bash
cp LMS-System/src/main/resources/application-secrets.properties.example \
   LMS-System/src/main/resources/application-secrets.properties
```

Or set environment variables directly:

```
DB_USERNAME=postgres
DB_PASSWORD=yourpassword
JWT_SECRET=your-secret-key-minimum-32-characters
MAIL_USERNAME=your-mailtrap-username
MAIL_PASSWORD=your-mailtrap-password
```

> **Email mode:** During development emails print to the console (`app.email.mode=console`). Change to `smtp` for real email delivery.

### 3. Run the Backend

```bash
cd LMS-System
./mvnw spring-boot:run
```

Backend runs on `http://localhost:8080`

### 4. Run the Frontend

```bash
cd LMS-Web
npm install
npm run dev
```

Frontend runs on `http://localhost:5173`

---

## API Overview

All protected endpoints require:
```
Authorization: Bearer <token>
```

### Auth (public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login and receive JWT |
| POST | `/api/auth/forgot-password` | Request OTP via email |
| POST | `/api/auth/verify-otp` | Verify OTP and reset password |

### Admin
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/admin/users` | Create employee account |
| GET | `/api/admin/users` | List all users |
| DELETE | `/api/admin/users/{id}` | Delete an employee |
| GET | `/api/admin/leave` | Get all leave requests |
| GET | `/api/admin/leave?status=PENDING` | Filter by status |
| PUT | `/api/admin/leave/action` | Approve or reject a request |
| GET | `/api/admin/leave-types` | List all leave types |
| PUT | `/api/admin/leave-types/{id}` | Rename a leave type |
| DELETE | `/api/admin/leave-types/{id}` | Delete a leave type |

### Employee
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/employee/leave/apply` | Apply for leave |
| GET | `/api/employee/leave` | View own leave history |
| DELETE | `/api/employee/leave/{id}/cancel` | Cancel a pending request |
| GET | `/api/employee/leave/balances` | View remaining leave days |

---

## Security Notes

- Passwords are BCrypt hashed — never stored as plaintext
- JWT secret is injected via environment variable — never hardcoded
- `application-secrets.properties` is gitignored — real credentials never committed
- Login error messages are intentionally generic to prevent username enumeration
- OTPs expire after 3 minutes and are single-use only

---

## Leave Request Lifecycle

```
Employee applies → PENDING
    Admin approves → APPROVED  (balance deducted)
    Admin rejects  → REJECTED  (balance unchanged)
    Employee cancels → deleted (only while PENDING)
```

---

## Author

Built by **[Nkosinathi Mahlangu]**  