# User Authentication Service (Module A) - Java Spring Boot Edition

## Overview

Core authentication and user management service written in **Java Spring Boot 3.2.0**. This is the foundational microservice that all other services depend on for user authentication, JWT token generation/validation, and user profile management.

**Repository**: https://github.com/jhakeshab/java-finflow-auth-service  
**Language**: Java 17  
**Framework**: Spring Boot 3.2.0  
**Database**: PostgreSQL 15  
**Cache**: Redis 7  
**Port**: 8080 (configurable via `SERVER_PORT`)  

---

## Features

✅ **User Registration** - Register new users with email, password, first name, last name, phone number  
✅ **User Login** - Authenticate users and generate JWT tokens  
✅ **JWT Token Management** - Generate and validate JSON Web Tokens using JJWT library  
✅ **User Profile Management** - Get and update user details  
✅ **User Status Management** - Track user states (ACTIVE, INACTIVE, SUSPENDED, DELETED)  
✅ **KYC Status Tracking** - Track Know Your Customer verification status (PENDING, IN_PROGRESS, VERIFIED, REJECTED)  
✅ **Role-Based Access Control** - Support for multiple user roles  
✅ **Session Caching** - Redis-based user caching with 10-minute TTL  
✅ **Security** - BCrypt password hashing with strength 10, JWT token security  
✅ **Health Checks** - Service health status endpoint  
✅ **Exception Handling** - Comprehensive error handling with proper HTTP status codes  
✅ **Input Validation** - Request validation using Jakarta validation annotations  

---

## Dependencies

### Direct Dependencies
- **None** - This is the foundational/core service

### Services That Depend on This
1. **Wallet Service** - Validates user KYC status before wallet creation
2. **Payment Service** - Verifies user and KYC status
3. **Notification Service** - Gets user contact information
4. **KYC/AML Service** - Updates user KYC status
5. **Reporting Service** - Retrieves user data for reports

---

## Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 17 |
| Framework | Spring Boot | 3.2.0 |
| Security | Spring Security | 6.2.0 |
| Database | PostgreSQL | 15 |
| ORM | Spring Data JPA / Hibernate | 6.2.0 |
| Cache | Redis | 7 |
| JWT | JJWT | 0.12.5 |
| Validation | Jakarta Validation | 3.0.2 |
| Build Tool | Maven | 3.9+ |
| Container | Docker | Latest |
| Password Encoding | BCrypt | Spring Security (Strength 10) |

---

## Project Structure

```
finflow-auth-service-java/
├── pom.xml                              # Maven dependencies
├── Dockerfile                           # Multi-stage Docker build
├── .dockerignore                        # Docker ignore file
│
├── src/main/java/com/finflow/auth/
│   ├── AuthServiceApplication.java      # Spring Boot entry point
│   │
│   ├── controller/
│   │   ├── AuthController.java          # Auth REST endpoints
│   │   └── HealthController.java        # Health check endpoint
│   │
│   ├── service/
│   │   ├── AuthService.java             # Authentication logic
│   │   └── UserService.java             # User management logic
│   │
│   ├── entity/
│   │   ├── User.java                    # User JPA entity
│   │   └── Role.java                    # Role JPA entity
│   │
│   ├── repository/
│   │   ├── UserRepository.java          # User data access
│   │   └── RoleRepository.java          # Role data access
│   │
│   ├── dto/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── TokenResponse.java
│   │   ├── UserDTO.java
│   │   ├── ApiResponse.java
│   │   └── UpdateProfileRequest.java
│   │
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── UserAlreadyExistsException.java
│   │   ├── InvalidCredentialsException.java
│   │   ├── UserNotFoundException.java
│   │   └── UserSuspendedException.java
│   │
│   ├── util/
│   │   └── JwtUtil.java                 # JWT token utility
│   │
│   └── config/
│       └── SecurityConfig.java          # Security configuration
│
├── src/main/resources/
│   └── application.yml                  # Spring Boot configuration
│
└── src/test/java/                       # Unit tests
```

---

## Installation

### Prerequisites

- **Java 17+** installed
- **Maven 3.9+** installed  
- **PostgreSQL 15+** running
- **Redis 7+** running (for caching)
- **Docker** (optional, for containerized deployment)

### Option 1: Build with Maven (Local Development)

```bash
# Clone the repository
git clone https://github.com/jhakeshab/java-finflow-auth-service.git
cd java-finflow-auth-service

# Verify Java and Maven
java -version
mvn -version

# Build the project
mvn clean package

# Run locally
java -jar target/auth-service-1.0.0.jar
```

Service will be available at: `http://localhost:8080/api`

### Option 2: Maven Spring Boot Run (Faster for Development)

```bash
# Run directly without building JAR
mvn spring-boot:run
```

### Option 3: Docker Build and Run

```bash
# Build Docker image
docker build -t finflow/auth-service:1.0.0 .

# Run container
docker run -d \
  --name auth-service \
  -p 9001:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/auth_service_db \
  -e SPRING_DATASOURCE_USERNAME=auth_user \
  -e SPRING_DATASOURCE_PASSWORD=auth_password \
  -e SPRING_REDIS_HOST=localhost \
  -e SPRING_REDIS_PORT=6379 \
  -e JWT_SECRET=your-super-secret-key-min-32-chars \
  finflow/auth-service:1.0.0
```

Service will be available at: `http://localhost:9001/api`

### Option 4: Docker Compose (Full Stack)

```bash
# From finflow-java-services root directory
docker-compose up -d auth-service-java

# Verify
docker-compose ps
```

---

## Configuration

### Environment Variables

Configure via environment variables or `.env` file:

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/auth_service_db
SPRING_DATASOURCE_USERNAME=auth_user
SPRING_DATASOURCE_PASSWORD=auth_password

# Redis
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379

# JWT
JWT_SECRET=your-super-secret-jwt-key-min-32-characters-long
JWT_EXPIRATION=3600

# Server
SERVER_PORT=8080

# Hibernate
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

### application.yml Configuration

The service is configured via `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/auth_service_db
    username: auth_user
    password: auth_password
  redis:
    host: localhost
    port: 6379
  cache:
    type: redis
    redis:
      time-to-live: 600000  # 10 minutes in milliseconds

jwt:
  secret: your-super-secret-jwt-key-min-32-characters
  expiration: 3600  # 1 hour in seconds

server:
  port: 8080
```

### Database Connection Pool (Hikari)

Default pool configuration in `application.yml`:
- Maximum pool size: 10
- Minimum idle: 5
- Connection timeout: 20 seconds
- Idle timeout: 5 minutes
- Max lifetime: 20 minutes

---

## Running the Service

### Development

```bash
# Using Maven Spring Boot plugin
mvn spring-boot:run

# Or run the JAR directly
java -jar target/auth-service-1.0.0.jar

# Specify custom port
java -Dserver.port=9001 -jar target/auth-service-1.0.0.jar

# With custom JWT secret
java -DJWT_SECRET=your-custom-secret -jar target/auth-service-1.0.0.jar
```

### Production (Docker)

```bash
# Build optimized image
docker build -t finflow/auth-service:latest .

# Run with proper configuration
docker run -d \
  --name auth-service \
  -p 9001:8080 \
  --network finflow-network \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://auth-postgres:5432/auth_service_db \
  -e SPRING_DATASOURCE_USERNAME=auth_user \
  -e SPRING_DATASOURCE_PASSWORD=auth_password \
  -e SPRING_REDIS_HOST=auth-redis \
  -e SPRING_REDIS_PORT=6379 \
  -e JWT_SECRET=production-secret-key-change-this \
  -e JWT_EXPIRATION=3600 \
  -e SPRING_JPA_HIBERNATE_DDL_AUTO=validate \
  -v logs:/app/logs \
  finflow/auth-service:latest
```

### Verify Service is Running

```bash
# Check health endpoint
curl http://localhost:8080/health

# Check application info
curl http://localhost:8080/actuator/health

# View logs
docker logs -f auth-service
```

---

## API Endpoints

### Base URL
```
http://localhost:8080/api  (Development)
http://localhost:9001/api  (Docker)
```

### 1. Register New User

```http
POST /auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePassword123!",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+12025551234"
}
```

**Response (201 Created)**
```json
{
  "status": "success",
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+12025551234",
    "status": "ACTIVE",
    "kycStatus": "PENDING",
    "roles": []
  },
  "timestamp": 1704067200000
}
```

**Validation Rules:**
- Email must be valid format and unique
- Password minimum 8 characters
- First name and last name required
- Phone number format: 10-15 digits with optional +

**Error Responses:**
- `409 Conflict` - Email already registered
- `400 Bad Request` - Invalid input data

---

### 2. User Login

```http
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePassword123!"
}
```

**Response (200 OK)**
```json
{
  "status": "success",
  "message": "Login successful",
  "data": {
    "access_token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzA0MDY3MjAwLCJleHAiOjE3MDQwNzA4MDB9...",
    "token_type": "Bearer",
    "expires_in": 3600,
    "user": {
      "id": 1,
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "phoneNumber": "+12025551234",
      "status": "ACTIVE",
      "kycStatus": "PENDING",
      "roles": []
    }
  },
  "timestamp": 1704067200000
}
```

**Error Responses:**
- `401 Unauthorized` - Invalid email or password
- `403 Forbidden` - User account suspended

---

### 3. Get User Details

```http
GET /auth/user/{userId}
Authorization: Bearer <jwt_token>
```

**Response (200 OK)**
```json
{
  "status": "success",
  "message": "User retrieved",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+12025551234",
    "status": "ACTIVE",
    "kycStatus": "PENDING",
    "roles": []
  },
  "timestamp": 1704067200000
}
```

**Error Responses:**
- `404 Not Found` - User not found

---

### 4. Update User Profile

```http
PUT /auth/user/{userId}
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "firstName": "Jane",
  "lastName": "Smith",
  "phoneNumber": "+12025559876"
}
```

All fields are optional. Only provided fields are updated.

**Response (200 OK)**
```json
{
  "status": "success",
  "message": "Profile updated",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "Jane",
    "lastName": "Smith",
    "phoneNumber": "+12025559876",
    "status": "ACTIVE",
    "kycStatus": "PENDING",
    "roles": []
  },
  "timestamp": 1704067200000
}
```

---

### 5. Verify JWT Token

```http
GET /auth/verify-token
Authorization: Bearer <jwt_token>
```

**Response (200 OK)**
```json
{
  "status": "success",
  "message": "Token verified",
  "data": true,
  "timestamp": 1704067200000
}
```

**Response if invalid (200 OK)**
```json
{
  "status": "success",
  "message": "Token verified",
  "data": false,
  "timestamp": 1704067200000
}
```

---

### 6. Health Check

```http
GET /health
```

**Response (200 OK)**
```json
{
  "status": "UP",
  "service": "Auth Service",
  "timestamp": 1704067200000
}
```

---

## Database Schema

### Users Table

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    kyc_status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### Roles Table

```sql
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);
```

### User_Roles Junction Table

```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);
```

**Note:** Tables are automatically created by Hibernate on first run via `ddl-auto: update` setting.

---

## User and KYC Status Enums

### User Status
- `ACTIVE` - User account is active (default)
- `INACTIVE` - User account is inactive
- `SUSPENDED` - User account is suspended (cannot login)
- `DELETED` - User account is deleted

### KYC Status
- `PENDING` - KYC verification not started (default)
- `IN_PROGRESS` - KYC verification in progress
- `VERIFIED` - User has been KYC verified
- `REJECTED` - KYC verification was rejected

---

## Testing

### Using cURL

```bash
# 1. Register a user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test@12345",
    "firstName": "Test",
    "lastName": "User",
    "phoneNumber": "+12345678901"
  }'

# 2. Login (save the access_token from response)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test@12345"
  }'

# 3. Export token
export TOKEN="eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."

# 4. Get user details
curl -X GET http://localhost:8080/api/auth/user/1 \
  -H "Authorization: Bearer $TOKEN"

# 5. Update profile
curl -X PUT http://localhost:8080/api/auth/user/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "firstName": "Updated",
    "lastName": "Name"
  }'

# 6. Verify token
curl -X GET http://localhost:8080/api/auth/verify-token \
  -H "Authorization: Bearer $TOKEN"

# 7. Check health
curl http://localhost:8080/health
```

### Running Tests

```bash
# Run all tests
mvn test

# Run with verbose output
mvn test -X

# Run specific test class
mvn test -Dtest=AuthControllerTest

# Run with coverage report
mvn test jacoco:report
# View report at: target/site/jacoco/index.html
```

---

## Security

### Password Security
- Hashed using BCrypt with strength 10
- Never stored or transmitted in plain text
- Minimum 8 characters required
- Not included in any API responses

### JWT Token Security
- Signed with HS512 algorithm
- Secret key must be > 32 characters
- Default expiration: 1 hour (configurable)
- Include token in `Authorization: Bearer <token>` header

### Database Security
- Connection pooling via Hikari CP
- Parameterized queries prevent SQL injection
- Unique constraints on email and phone number
- Encrypted password storage with bcrypt

### API Security
- Input validation on all endpoints
- Error messages don't expose sensitive info
- HTTP status codes indicate actual errors
- Support for HTTPS in production

---

## Performance Characteristics

| Metric | Value |
|--------|-------|
| Startup Time | ~15 seconds |
| Memory Usage (idle) | ~300MB |
| Memory Usage (active) | ~500MB |
| Registration time | ~100ms |
| Login time | ~150ms |
| Token validation | ~10ms |
| Database pool size | 10 connections |
| Cache TTL | 10 minutes |
| JWT expiration | 1 hour (default) |

---

## Monitoring

### Health Endpoint

```bash
# Check service health
curl http://localhost:8080/health

# Detailed health info
curl http://localhost:8080/actuator/health
```

### Application Logs

Logs are configured to output to:
- **Console** - Real-time output
- **File** - `logs/auth-service.log` (rotated daily, max 10 files)

Log levels:
- `ROOT` - INFO level
- `com.finflow` - DEBUG level
- `org.springframework.security` - DEBUG level

View logs:
```bash
# Console logs
tail -f logs/auth-service.log

# Docker container logs
docker logs -f auth-service
```

### Metrics

Access application metrics at:
```
http://localhost:8080/actuator/metrics
```

Available metrics:
```
http://localhost:8080/actuator/metrics/http.server.requests
http://localhost:8080/actuator/metrics/jvm.memory.used
http://localhost:8080/actuator/metrics/process.uptime
```

---

## Troubleshooting

### Service Won't Start

**Error: Connection refused: localhost:5432**
```
Solution: Ensure PostgreSQL is running
$ psql -h localhost -U postgres
```

**Error: Cannot connect to Redis**
```
Solution: Start Redis
$ redis-server
# Or with Docker:
$ docker run -d -p 6379:6379 redis:7
```

### Database Errors

**Error: org.hibernate.HibernateException: Access to DialectResolutionInfo**
```
Solution: 
1. Ensure database exists:
   $ createdb -U postgres auth_service_db

2. Verify environment variables:
   $ export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/auth_service_db
```

### JWT Token Errors

**Error: JWT signature does not match**
```
Solution: Ensure JWT_SECRET is consistent across all services
- Update JWT_SECRET environment variable
- Restart all services that validate tokens
```

### Build Failures

**Error: mvn: command not found**
```
Solution: Install Maven
- macOS: brew install maven
- Ubuntu: sudo apt-get install maven
- Windows: Download from maven.apache.org
```

---

## Development

### Adding New Endpoint

1. Create method in `AuthController.java`:
```java
@GetMapping("/new-endpoint")
public ResponseEntity<ApiResponse<Object>> newEndpoint() {
    return ResponseEntity.ok(ApiResponse.success("Success", data));
}
```

2. Add corresponding service method in `AuthService.java`

3. Create/update DTO if needed

4. Add tests

### Database Migrations

Tables are automatically created/updated via Hibernate `ddl-auto: update` setting:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # create | create-drop | update | validate
```

For production, use `validate` mode to prevent accidental schema changes.

---

## Comparison with Python FastAPI Version

| Aspect | Python FastAPI | Java Spring Boot |
|--------|-----------------|------------------|
| Startup Time | ~3-5s | ~15s |
| Memory Usage | ~150MB | ~300MB |
| Throughput | ~500 req/s | ~2000 req/s |
| Development Speed | Faster | Moderate |
| Production Ready | Good | Excellent |
| Type Safety | No | Yes |
| ORM | SQLAlchemy | Hibernate/JPA |
| Cache | Redis | Spring Data Redis |

---

## Related Services

All these services depend on Auth Service:

- **Wallet Service** (Port 9002) - Validates KYC before wallet creation
- **Payment Service** (Port 9003) - Checks user authentication for payments
- **KYC/AML Service** (Port 9004) - Updates user KYC status
- **Notification Service** (Port 9005) - Sends user notifications
- **Reporting Service** (Port 9006) - Aggregates user data

---

## FAQ

**Q: Can I run without Redis?**  
A: Yes, set `spring.cache.type: none` in `application.yml`. Performance will be reduced.

**Q: Can I use MySQL instead of PostgreSQL?**  
A: Yes, update `spring.datasource.url` and Hibernate dialect.

**Q: How do I reset the database?**  
A: Stop services, run `docker-compose down -v`, then restart.

**Q: How do I change JWT expiration time?**  
A: Set `JWT_EXPIRATION=7200` environment variable (in seconds).

**Q: Is this production-ready?**  
A: Yes, ensure:
- [ ] JWT_SECRET is strong (>32 chars)
- [ ] Use HTTPS/TLS in production
- [ ] Configure firewall rules
- [ ] Set up monitoring/alerting
- [ ] Enable database backups
- [ ] Use proper secret management

---

## Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [JJWT Library](https://github.com/jwtk/jjwt)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)
- [Redis Docs](https://redis.io/documentation)

---

## Version History

### v1.0.0 (Current)
- Java Spring Boot implementation
- Full feature parity with Python version
- Production-ready with enhanced performance

### Original Python Version
- See: https://github.com/jhakeshab/finflow-auth-service

---

## License

MIT License

---

## Support

For issues or questions:
- GitHub Issues: https://github.com/jhakeshab/java-finflow-auth-service/issues
- Email: jhakeshab@example.com

---

**Last Updated**: January 2025  
**Status**: ✅ Production Ready