# Library Management System - Backend

A RESTful API built with Spring Boot 3 for managing library operations, including book management, user authentication, and reservation handling.

## 🚀 Features

- **RESTful API**: Complete CRUD operations for books, categories, and users
- **JWT Authentication**: Secure token-based authentication and authorization
- **Role-Based Access Control**: USER and LIBRARIAN roles with different permissions
- **File Upload**: Image upload for book covers with validation
- **Database Migrations**: Flyway for version-controlled database schema
- **Email Notifications**: SMTP integration for email alerts (optional)
- **Exception Handling**: Global exception handling with custom exceptions
- **CORS Support**: Configured for frontend integration

## 📋 Prerequisites

Before you begin, ensure you have:

- **Java 17** or higher
- **Maven 3.6+**
- **MySQL 8.0+** (or compatible database)
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code)

## 🛠️ Installation & Setup

### 1. Clone the Repository

```bash
git clone <your-backend-repo-url>
cd LibraryMS_backend/backend
```

### 2. Database Setup

Create a MySQL database:

```sql
CREATE DATABASE new_lms_db;
```

### 3. Configure Database

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/new_lms_db?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=your_username
spring.datasource.password=your_password
```

**⚠️ Important**: Update these values with your MySQL credentials. Never commit sensitive passwords to Git!

### 4. Configure JWT Secret

Update JWT secret in `application.properties`:

```properties
jwt.secret=YourLongAndSecureSecretKeyHere
jwt.expiration=86400000  # 24 hours in milliseconds
```

### 5. Build the Project

```bash
mvn clean install
```

### 6. Run the Application

**Option 1: Using Maven**
```bash
mvn spring-boot:run
```

**Option 2: Using IDE**
- Run `BackendApplication.java` from your IDE

**Option 3: Using JAR**
```bash
mvn clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### 7. Verify it's Running

```bash
# Health check endpoint
curl http://localhost:8081/api/health
```

You should see: `Backend is running on port 8081`

## 📁 Project Structure

```
backend/
├── src/main/java/com/imashi/lms/backend/
│   ├── controller/              # REST Controllers
│   │   ├── AuthController.java         # Authentication endpoints
│   │   ├── BookController.java         # Book CRUD operations
│   │   ├── CategoryController.java     # Category management
│   │   ├── ReservationController.java  # Reservation operations
│   │   ├── LibrarianController.java    # Librarian-only endpoints
│   │   ├── UserController.java         # User management
│   │   └── FileUploadController.java   # File upload endpoints
│   ├── service/                 # Business Logic Layer
│   │   ├── AuthService.java            # Authentication logic
│   │   ├── BookService.java            # Book business logic
│   │   ├── CategoryService.java        # Category logic
│   │   ├── ReservationService.java     # Reservation logic
│   │   ├── LibrarianService.java       # Librarian operations
│   │   └── UserService.java            # User management
│   ├── repository/              # Data Access Layer (JPA)
│   │   ├── UserRepository.java
│   │   ├── BookRepository.java
│   │   ├── CategoryRepository.java
│   │   └── ReservationRepository.java
│   ├── entity/                  # JPA Entities
│   │   ├── User.java
│   │   ├── Book.java
│   │   ├── Category.java
│   │   ├── Reservation.java
│   │   ├── Role.java
│   │   ├── BookStatus.java
│   │   └── ReservationStatus.java
│   ├── dto/                     # Data Transfer Objects
│   │   ├── request/             # Request DTOs
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   └── ...
│   │   └── response/            # Response DTOs
│   │       ├── AuthResponse.java
│   │       └── ...
│   ├── security/                # Security Configuration
│   │   ├── SecurityConfig.java          # Spring Security config
│   │   ├── JwtTokenProvider.java        # JWT token generation/validation
│   │   ├── JwtAuthenticationFilter.java # JWT filter
│   │   └── CustomUserDetailsService.java # User details service
│   ├── exception/               # Exception Handling
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── ResourceAlreadyExistsException.java
│   │   └── UnauthorizedException.java
│   └── BackendApplication.java  # Main application class
├── src/main/resources/
│   ├── application.properties   # Application configuration
│   └── db/migration/            # Flyway database migrations
│       ├── V1__init_schema.sql
│       ├── V2__alter_users_id_to_bigint.sql
│       ├── V3__create_books_and_categories.sql
│       ├── V4__add_genre_language_and_reservations.sql
│       ├── V5__add_image_url_to_books.sql
│       └── V6__add_name_to_users.sql
└── pom.xml                      # Maven dependencies

```

## 🔧 Configuration

### Database Configuration

Edit `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/new_lms_db?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.open-in-view=false
```

### JWT Configuration

```properties
jwt.secret=YourLongAndSecureSecretKeyHere
jwt.expiration=86400000  # 24 hours in milliseconds
```

**Security Note**: Use a strong, random secret key in production!

### File Upload Configuration

```properties
file.upload-dir=uploads/books
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
```

### CORS Configuration

CORS is configured in `SecurityConfig.java` to allow:
- `http://localhost:3000`
- `http://localhost:3001`

To add more origins, update `SecurityConfig.java`.

### Email Configuration (Optional)

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**Note**: For Gmail, use an App Password, not your regular password.

## 📡 API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/register` | Register new user | No |
| POST | `/api/auth/login` | User login | No |

### Books (Public)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/books` | Get paginated books with filters | No |
| GET | `/api/books/{id}` | Get book by ID | No |
| GET | `/api/categories` | Get all categories | No |

### Reservations (User)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/reservations/my-reservations` | Get user's reservations | Yes (USER) |
| POST | `/api/user/books/{bookId}/reserve` | Reserve a book | Yes (USER) |
| DELETE | `/api/reservations/{id}` | Cancel reservation | Yes (USER) |
| POST | `/api/reservations/{id}/renew` | Renew reservation | Yes (USER) |

### Librarian Operations

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/librarian/books` | Add new book | Yes (LIBRARIAN) |
| PUT | `/api/books/{id}` | Update book | Yes (LIBRARIAN) |
| DELETE | `/api/books/{id}` | Delete book | Yes (LIBRARIAN) |
| POST | `/api/librarian/categories` | Add category | Yes (LIBRARIAN) |
| PUT | `/api/categories/{id}` | Update category | Yes (LIBRARIAN) |
| DELETE | `/api/categories/{id}` | Delete category | Yes (LIBRARIAN) |
| GET | `/api/librarian/users` | Get all users | Yes (LIBRARIAN) |
| PUT | `/api/librarian/users/{userId}/blacklist` | Blacklist/unblacklist user | Yes (LIBRARIAN) |

### File Upload

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/files/upload` | Upload book image | Yes (LIBRARIAN) |
| GET | `/api/files/{filename}` | Get uploaded file | No |

### Health Check

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/health` | Health check | No |

## 🔐 Security

- **JWT Authentication**: Token-based authentication
- **Role-Based Authorization**: USER and LIBRARIAN roles
- **Password Encryption**: BCrypt password hashing
- **CORS Configuration**: Configured for frontend integration
- **Request Validation**: Input validation with Spring Validation
- **SQL Injection Protection**: JPA/Hibernate parameterized queries

## 🗄️ Database Schema

The database schema is managed by Flyway migrations. Migrations run automatically on application startup.

### Tables

1. **users**: User accounts and authentication
   - id, email, password, name, role, is_blacklisted, created_at

2. **books**: Book information and metadata
   - id, title, author, isbn, category_id, status, total_copies, available_copies, description, genre, language, image_url, created_at, updated_at

3. **categories**: Book categories
   - id, name, description, created_at

4. **reservations**: Book reservations and due dates
   - id, user_id, book_id, reservation_date, due_date, return_date, status, created_at

## 🧪 Testing

Run tests:
```bash
mvn test
```

## 🐛 Troubleshooting

### Database Connection Error

**Error**: `Communications link failure` or `Access denied`

**Solution**:
1. Verify MySQL is running: `mysql -u root -p`
2. Check database credentials in `application.properties`
3. Ensure database exists: `CREATE DATABASE new_lms_db;`
4. Check MySQL port (default: 3306)

### Port Already in Use

**Error**: `Port 8081 is already in use`

**Solution**:
1. Change port in `application.properties`:
   ```properties
   server.port=8082
   ```
2. Or kill the process using port 8081:
   ```bash
   # Windows
   netstat -ano | findstr :8081
   taskkill /PID <PID> /F
   ```

### Flyway Migration Errors

**Error**: `Migration checksum mismatch` or `Migration failed`

**Solution**:
1. Check migration files are in correct order (V1, V2, V3...)
2. Verify database is clean or migrations are compatible
3. Check Flyway baseline settings
4. Manual fix: `DELETE FROM flyway_schema_history WHERE version='X'`

### JWT Token Issues

**Error**: `JWT signature does not match`

**Solution**:
- Ensure `jwt.secret` in `application.properties` matches across all services
- Check token expiration settings

### File Upload Errors

**Error**: `File size exceeds maximum`

**Solution**:
- Increase limits in `application.properties`:
  ```properties
  spring.servlet.multipart.max-file-size=10MB
  spring.servlet.multipart.max-request-size=10MB
  ```

## 📦 Key Dependencies

- **Spring Boot 3.5.8** - Core framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database access
- **MySQL Connector** - MySQL database driver
- **Flyway** - Database migrations
- **JWT (jjwt 0.12.5)** - JSON Web Tokens
- **Lombok** - Code generation
- **Spring Boot Starter Validation** - Input validation

## 🔄 API Response Format

### Success Response

```json
{
  "status": "success",
  "message": "Operation successful",
  "data": {
    // Response data
  }
}
```

### Error Response

```json
{
  "status": "error",
  "message": "Error description",
  "timestamp": "2024-01-01T00:00:00"
}
```

## 🔒 Security Best Practices

1. **Never commit sensitive data**: Use environment variables for passwords
2. **Use strong JWT secrets**: Generate random, long strings
3. **Enable HTTPS in production**: Never use HTTP for authentication
4. **Validate all inputs**: Prevent injection attacks
5. **Keep dependencies updated**: Regularly update Spring Boot and dependencies

## 📝 Environment Variables (Recommended)

For production, use environment variables instead of hardcoded values:

```properties
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
```

## 🚀 Production Deployment

1. Build JAR: `mvn clean package`
2. Set environment variables
3. Configure production database
4. Update CORS allowed origins
5. Enable HTTPS
6. Configure logging
7. Set up monitoring

## 📄 License

This project is part of an internship assignment.

## 👤 Author

Developed as part of Library Management System internship project.

## 📞 Support

For issues or questions, please check the frontend documentation or open an issue on GitHub.

