# Leave Management System (LMS)

A full-stack Leave Management System built with Spring Boot and React. Employees can apply for leave and track their requests, while admins manage users, leave types, and approve or reject applications.

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

Create a PostgreSQL database named `lms`, then run the app once to let Hibernate create the tables. After that seed the reference data:

```sql
-- Roles
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('EMPLOYEE');

-- Leave Types
INSERT INTO leave_types (name, default_days) VALUES ('Annual Leave', 21);
INSERT INTO leave_types (name, default_days) VALUES ('Sick Leave', 10);
INSERT INTO leave_types (name, default_days) VALUES ('Family Responsibility Leave', 3);

-- Admin user (password: Admin@123)
INSERT INTO users (first_name, last_name, email, username, password, enabled)
VALUES ('Nkosinathi', 'Mahlangu', 'admin@lmssystem.com', 'admin',
'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lmmW', true);

INSERT INTO user_roles (user_id, role_id)
VALUES (
    (SELECT user_id FROM users WHERE username = 'admin'),
    (SELECT id FROM roles WHERE name = 'ADMIN')
);
```

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
| POST | `/auth/login` | Login and receive JWT |
| POST | `/auth/forgot-password` | Request OTP via email |
| POST | `/auth/verify-otp` | Verify OTP and reset password |

### Admin
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/admin/users` | Create employee account |
| GET | `/admin/users` | List all users |
| DELETE | `/admin/users/{id}` | Delete an employee |
| GET | `/admin/leave` | Get all leave requests |
| GET | `/admin/leave?status=PENDING` | Filter by status |
| PUT | `/admin/leave/action` | Approve or reject a request |
| GET | `/admin/leave-types` | List all leave types |
| PUT | `/admin/leave-types/{id}` | Rename a leave type |
| DELETE | `/admin/leave-types/{id}` | Delete a leave type |

### Employee
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/employee/leave/apply` | Apply for leave |
| GET | `/employee/leave` | View own leave history |
| DELETE | `/employee/leave/{id}/cancel` | Cancel a pending request |
| GET | `/employee/leave/balances` | View remaining leave days |

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

Built by **[Your Name]**  
[GitHub](https://github.com/yourusername) · [LinkedIn](https://linkedin.com/in/yourprofile)
