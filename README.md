# Resource Booking System - RESTful Backend API

A production-grade, secure, RESTful **Resource Booking System** built with **Spring Boot 3 (Java 17+)**, **Spring Security 6**, **JWT Authentication**, **Spring Data JPA**, and **H2/MySQL/PostgreSQL**.

The system enables regular users to view resources and manage their own reservations, while administrators have full privilege to manage resources and all reservations across the system.

---

## Key Features

- **JWT Authentication & Stateless Security**: Secure login authentication endpoint (`POST /auth/login`) issuing signed JWT tokens with BCrypt password hashing.
- **Role-Based Access Control (RBAC)**: Enforces `ROLE_ADMIN` and `ROLE_USER` permissions.
- **Secure Reservation Ownership**: User identity is strictly extracted from the validated JWT token (`SecurityContextHolder`), preventing any client-side request body tampering.
- **Decimal Monetary Calculation**: Reservation `totalPrice` is calculated dynamically based on resource hourly rates and duration, stored with `BigDecimal` precision.
- **Advanced Dynamic Filtering**: Filter reservations by `status` (`PENDING`, `CONFIRMED`, `CANCELLED`), `minPrice`, and `maxPrice` powered by Spring Data JPA Specifications.
- **Pagination & Sorting**: Paginated results with `page`, `size`, `sortBy`, and `sortDir` parameters.
- **Double Booking Prevention**: Validates start/end date logic and checks for overlapping bookings before confirming reservations.
- **Multi-Database Support**: Ready to run out-of-the-box with H2 in-memory DB or connect to MySQL / PostgreSQL seamlessly.
- **Swagger / OpenAPI Documentation**: Interactive API documentation at `/swagger-ui.html` with Bearer token authentication configured.
- **Automated Data Seeding**: Automatically creates default ADMIN and USER accounts along with sample resources and reservations on startup.

---

## Seed Users for Testing

Upon application startup, the system automatically populates the database with the following credentials:

| Role | Email | Password | Permissions |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin@example.com` | `Admin@123` | Full CRUD on resources & all reservations |
| **USER** | `user@example.com` | `User@123` | Read-only resources, Create/View/Cancel own reservations |
| **USER** | `jane@example.com` | `User@123` | Read-only resources, Create/View/Cancel own reservations |

---

## Prerequisites

- **Java 17** or higher installed.
- **Maven** (bundled via included Maven Wrapper `./mvnw` or `.\mvnw.cmd`).

---

## Quick Start & Setup Instructions

### 1. Clone & Build the Application
```bash
# Clone the repository
git clone <repository-url>
cd EXELYNT-BACKEND-ASSIGNMENT

# Build project and run test suite
.\mvnw.cmd clean test
```

### 2. Run the Application
```bash
# Start the Spring Boot application (Uses H2 in-memory DB by default)
.\mvnw.cmd spring-boot:run
```
The server will start on **`http://localhost:8080`**.

---

## API Documentation & Swagger UI

Once the application is running, access the interactive Swagger UI documentation at:
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### How to Authenticate in Swagger UI:
1. Execute `POST /auth/login` with `admin@example.com` / `Admin@123` (or `user@example.com` / `User@123`).
2. Copy the returned `token` string.
3. Click the **Authorize** button at the top right of Swagger UI.
4. Enter the token into the value box and click **Authorize**.

---

## Postman Collection

A pre-configured Postman Collection file `booking-system-postman_collection.json` is included in the project root directory.

### To Import into Postman:
1. Open Postman -> Click **Import**.
2. Select `booking-system-postman_collection.json`.
3. Run `Admin Login` or `User Login` — the JWT token will automatically populate in environment variables for subsequent requests!

---

## Database Configuration & Environment Variables

### 1. In-Memory H2 Database (Test Suite & Development)
The automated test suite (`.\mvnw.cmd test`) uses an isolated H2 in-memory database configured in `src/test/resources/application.properties`.

### 2. MySQL Configuration (Production)
Set the following environment variables when running in production:
```bash
DB_URL=jdbc:mysql://localhost:3306/bookingdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=your_secure_password
DB_DRIVER=com.mysql.cj.jdbc.Driver
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
```

### 3. PostgreSQL Configuration (Production)
```bash
DB_URL=jdbc:postgresql://localhost:5432/bookingdb
DB_USERNAME=postgres
DB_PASSWORD=your_secure_password
DB_DRIVER=org.postgresql.Driver
```

---

## API Endpoints Summary

### Authentication (`/auth`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/login` | Public | Authenticates user & returns JWT token |
| `POST` | `/auth/register` | Public | Registers a new user account |

### Resources (`/resources`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/resources` | USER, ADMIN | List all bookable resources |
| `GET` | `/resources/{id}` | USER, ADMIN | Get resource details by ID |
| `POST` | `/resources` | ADMIN | Create a new resource |
| `PUT` | `/resources/{id}` | ADMIN | Update resource details |
| `DELETE` | `/resources/{id}` | ADMIN | Delete a resource |

### Reservations (`/reservations`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/reservations` | USER, ADMIN | Create a reservation (Identity taken from JWT) |
| `GET` | `/reservations` | USER, ADMIN | Get reservations with status/price filtering, pagination & sorting |
| `GET` | `/reservations/{id}` | USER, ADMIN | Get reservation details (USER: own only; ADMIN: any) |
| `PUT` | `/reservations/{id}/status`| USER, ADMIN | Update status (USER: cancel own; ADMIN: any status) |
| `DELETE` | `/reservations/{id}` | ADMIN | Delete reservation |

#### Query Parameters for `GET /reservations`:
- `status`: `PENDING`, `CONFIRMED`, `CANCELLED` (Optional)
- `minPrice`: Decimal value (Optional, e.g. `50.00`)
- `maxPrice`: Decimal value (Optional, e.g. `500.00`)
- `page`: Page index starting from `0` (Default: `0`)
- `size`: Items per page (Default: `10`)
- `sortBy`: Field to sort by (Default: `createdAt`)
- `sortDir`: `asc` or `desc` (Default: `desc`)

---

## Running Automated Tests

Run the full automated test suite using Maven:
```bash
.\mvnw.cmd test
```
The test suite covers:
- **Unit Tests**: `AuthServiceTest`, `ReservationServiceTest` (Authentication, JWT user binding, price decimal calculation, date validation, overlap prevention, status transitions).
- **Integration Tests**: `ResourceControllerIntegrationTest`, `ReservationControllerIntegrationTest` (Spring Security MockMvc RBAC verification, validation responses, pagination & sorting).
