<!-- 80b9d578-1c61-4ed4-8867-bfa08fef2fc8 83529871-b97d-4a1c-9545-3ac269ff9c7b -->
# Library Management System - Complete Implementation Plan

## System Architecture Overview

The system consists of three main components:

1. **Backend Service** (Spring Boot) - Port 8081
2. **API Gateway** (Spring Cloud Gateway) - Port 8080
3. **Frontend Application** (Next.js) - Port 3000

Communication flow: Frontend → API Gateway → Backend Service

---

## Phase 1: Backend Implementation (Spring Boot)

### 1.1 Database Schema & Migrations

**Files to create:**

- `backend/src/main/resources/db/migration/V1__init_schema.sql` (update existing)
- `backend/src/main/resources/db/migration/V2__create_categories_table.sql`
- `backend/src/main/resources/db/migration/V3__create_books_table.sql`
- `backend/src/main/resources/db/migration/V4__create_reservations_table.sql`

**Tasks:**

- Update V1 migration to use ENUM for role field: `ENUM('LIBRARIAN','USER')`
- Create categories table with id, name (UNIQUE)
- Create books table with all specified fields including foreign key to categories
- Create reservations table with foreign keys to users and books
- Add proper indexes for performance

### 1.2 Entity Classes (JPA Entities)

**Package structure:** `com.imashi.lms.backend.entity`

**Files to create:**

- `User.java` - Map to users table with role enum
- `Category.java` - Map to categories table
- `Book.java` - Map to books table with @ManyToOne to Category, status enum
- `Reservation.java` - Map to reservations table with @ManyToOne to User and Book, status enum

**Key annotations:**

- Use Lombok annotations (@Entity, @Data, @NoArgsConstructor, @AllArgsConstructor)
- Proper relationships with @ManyToOne, @OneToMany
- Enum types for status and role fields
- Validation annotations (@NotNull, @NotBlank, @Email)

### 1.3 Repository Layer (Spring Data JPA)

**Package structure:** `com.imashi.lms.backend.repository`

**Files to create:**

- `UserRepository.java` - Extends JpaRepository<User, Long>
- Methods: findByEmail, existsByEmail, findByRole
- `CategoryRepository.java` - Extends JpaRepository<Category, Long>
- Methods: findByName, existsByName
- `BookRepository.java` - Extends JpaRepository<Book, Long>
- Methods: findByCategory, findByStatus, findByAuthor, findByGenre, findByLanguage, findByCategoryId
- Custom query methods for search/filter
- `ReservationRepository.java` - Extends JpaRepository<Reservation, Long>
- Methods: findByUserId, findByBookId, findByStatus, findByUserIdAndStatus
- Custom queries for active reservations

### 1.4 DTOs (Data Transfer Objects)

**Package structure:** `com.imashi.lms.backend.dto`

**Request DTOs:**

- `LoginRequest.java` - email, password
- `RegisterRequest.java` - email, password, role
- `BookRequest.java` - title, author, genre, language, isbn, categoryId, image (MultipartFile)
- `CategoryRequest.java` - name
- `ReservationRequest.java` - bookId, reservationDays (7, 14, or 21)

**Response DTOs:**

- `AuthResponse.java` - token, user info
- `UserResponse.java` - id, email, role, isBlacklisted, createdAt
- `BookResponse.java` - All book fields including category name
- `CategoryResponse.java` - id, name
- `ReservationResponse.java` - All reservation fields with user and book details

### 1.5 Security & JWT Implementation

**Package structure:** `com.imashi.lms.backend.security`

**Files to create:**

- `JwtTokenProvider.java` - Generate and validate JWT tokens
- `JwtAuthenticationFilter.java` - Filter to process JWT tokens in requests
- `SecurityConfig.java` - Spring Security configuration
- Permit public endpoints (login, register, health)
- Secure admin endpoints with LIBRARIAN role
- Secure user endpoints with USER role
- Password encoder configuration
- CORS configuration
- `CustomUserDetailsService.java` - Load user by email for authentication

**Dependencies to add:**

- `spring-boot-starter-security`
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (JWT libraries)

### 1.6 Service Layer

**Package structure:** `com.imashi.lms.backend.service`

**Files to create:**

- `AuthService.java`
- register() - Create new user with hashed password
- login() - Authenticate and generate JWT token
- validateToken() - Validate JWT token

- `UserService.java`
- getAllUsers() - List all users (LIBRARIAN only)
- blacklistUser() - Toggle blacklist status (LIBRARIAN only)
- getUserProfile() - Get current user profile

- `CategoryService.java`
- getAllCategories() - List all categories
- getCategoryById() - Get single category
- createCategory() - Create new category (LIBRARIAN only)
- updateCategory() - Update category (LIBRARIAN only)
- deleteCategory() - Delete category if no books (LIBRARIAN only)

- `BookService.java`
- getAllBooks() - List all books with pagination
- getBookById() - Get single book with details
- searchBooks() - Search by title, author, genre, language
- filterBooks() - Filter by category, status, author, genre, language
- createBook() - Create book with image upload (LIBRARIAN only)
- updateBook() - Update book details and image (LIBRARIAN only)
- deleteBook() - Delete book if no active reservations (LIBRARIAN only)
- updateBookStatus() - Manually update status (LIBRARIAN only)

- `ReservationService.java`
- createReservation() - Create reservation (calculate due date)
- getUserReservations() - Get user's reservations
- getAllReservations() - Get all reservations (LIBRARIAN only)
- returnBook() - Mark reservation as returned (LIBRARIAN only)
- cancelReservation() - Cancel active reservation

- `FileStorageService.java`
- uploadBookImage() - Save uploaded book cover images
- deleteBookImage() - Remove image file
- getImageUrl() - Generate URL for image access

- `EmailService.java`
- sendReservationConfirmation() - Email on reservation
- sendReturnReminder() - Email reminder before due date
- Configure Spring Boot Mail properties

### 1.7 Controller Layer (REST APIs)

**Package structure:** `com.imashi.lms.backend.controller`

**Files to create:**

- `AuthController.java`
- POST `/api/auth/register` - Public registration
- POST `/api/auth/login` - Public login

- `UserController.java`
- GET `/api/users/profile` - Get current user profile
- GET `/api/users` - Get all users (LIBRARIAN)
- PUT `/api/users/{id}/blacklist` - Toggle blacklist (LIBRARIAN)

- `CategoryController.java`
- GET `/api/categories` - Get all categories
- GET `/api/categories/{id}` - Get category by id
- POST `/api/categories` - Create category (LIBRARIAN)
- PUT `/api/categories/{id}` - Update category (LIBRARIAN)
- DELETE `/api/categories/{id}` - Delete category (LIBRARIAN)

- `BookController.java`
- GET `/api/books` - Get all books (with pagination, search, filters)
- GET `/api/books/{id}` - Get book by id
- POST `/api/books` - Create book with image (LIBRARIAN)
- PUT `/api/books/{id}` - Update book (LIBRARIAN)
- DELETE `/api/books/{id}` - Delete book (LIBRARIAN)
- PUT `/api/books/{id}/status` - Update book status (LIBRARIAN)

- `ReservationController.java`
- GET `/api/reservations/my-reservations` - Get user's reservations
- GET `/api/reservations` - Get all reservations (LIBRARIAN)
- POST `/api/reservations` - Create reservation
- PUT `/api/reservations/{id}/return` - Return book (LIBRARIAN)
- DELETE `/api/reservations/{id}` - Cancel reservation

- `FileUploadController.java`
- GET `/api/files/{filename}` - Serve uploaded images

### 1.8 Configuration & Utilities

**Files to create:**

- `backend/src/main/resources/application.properties` updates:
- JWT secret and expiration
- File upload directory and size limits
- Email configuration (SMTP settings)
- CORS allowed origins

- `GlobalExceptionHandler.java` - Handle exceptions globally
- `ResponseEntity.java` wrapper for consistent API responses

### 1.9 Dependencies to Add

**Update `backend/pom.xml`:**

- `spring-boot-starter-security` (already have test dependency, need main)
- `spring-boot-starter-mail`
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (latest versions)
- `commons-io` (file handling)
- Validation dependencies

---

## Phase 2: API Gateway Implementation (Spring Cloud Gateway)

### 2.1 Gateway Project Setup

**Files to create:**

- `gateway/pom.xml` - Spring Cloud Gateway dependencies
- `gateway/src/main/java/.../GatewayApplication.java`
- `gateway/src/main/resources/application.properties`

**Dependencies:**

- `spring-cloud-starter-gateway`
- `spring-cloud-starter-netflix-eureka-client` (optional, for service discovery)
- JWT validation libraries

### 2.2 Gateway Configuration

**Files to create:**

- `GatewayConfig.java` - Route configuration
- Route `/api/backend/**` → `http://localhost:8081`
- CORS configuration
- Request/Response logging

- `JwtAuthenticationFilter.java` - Validate JWT tokens before routing
- `CorsConfig.java` - CORS configuration for gateway

**Route Rules:**

- Public endpoints: `/api/auth/**`, `/api/health`
- Secured endpoints: All other `/api/**` routes
- Strip `/api/backend` prefix when forwarding to backend

---

## Phase 3: Frontend Implementation (Next.js)

### 3.1 Project Setup

**Initialize Next.js project:**

- Create `frontend/` directory structure
- Setup Tailwind CSS
- Install component library (Shadcn/ui recommended for modern UI)
- Install axios for API calls
- Setup environment variables for API Gateway URL

### 3.2 Authentication & State Management

**Files to create:**

- `frontend/src/contexts/AuthContext.jsx` - Global auth state
- `frontend/src/hooks/useAuth.js` - Auth hook
- `frontend/src/utils/auth.js` - Token storage utilities
- `frontend/src/utils/api.js` - Axios instance with interceptors

### 3.3 Authentication Pages

**Files to create:**

- `frontend/src/app/login/page.jsx` - Login page
- `frontend/src/app/register/page.jsx` - Registration page
- `frontend/src/components/auth/LoginForm.jsx`
- `frontend/src/components/auth/RegisterForm.jsx`

### 3.4 Librarian Dashboard & Features

**Files to create:**

- `frontend/src/app/librarian/dashboard/page.jsx` - Admin dashboard
- Statistics cards (total books, users, reservations)
- Recent reservations table
- Quick actions

- `frontend/src/app/librarian/books/page.jsx` - Book management
- Book list with search and filters
- Create/Edit book modal/form
- Image upload for book covers
- Delete confirmation

- `frontend/src/app/librarian/categories/page.jsx` - Category management
- Category list
- Create/Edit/Delete categories

- `frontend/src/app/librarian/users/page.jsx` - User management
- User list with blacklist toggle
- User search/filter

- `frontend/src/app/librarian/reservations/page.jsx` - All reservations view
- Reservation list with filters
- Return book functionality

### 3.5 User Dashboard & Features

**Files to create:**

- `frontend/src/app/user/dashboard/page.jsx` - User dashboard
- Available books browsing
- My reservations section

- `frontend/src/app/user/books/page.jsx` - Book catalog
- Grid/list view of books
- Advanced search and filters (category, author, genre, language)
- Book detail modal/page
- Reserve book functionality

- `frontend/src/app/user/reservations/page.jsx` - My reservations
- Active reservations list
- Reservation history
- Due dates display

- `frontend/src/app/user/profile/page.jsx` - User profile

### 3.6 Shared Components

**Files to create:**

- `frontend/src/components/layout/Navbar.jsx` - Navigation bar
- `frontend/src/components/layout/Sidebar.jsx` - Sidebar for dashboards
- `frontend/src/components/common/BookCard.jsx` - Book display card
- `frontend/src/components/common/SearchBar.jsx` - Search component
- `frontend/src/components/common/FilterPanel.jsx` - Filter component
- `frontend/src/components/common/LoadingSpinner.jsx`
- `frontend/src/components/common/ErrorBoundary.jsx`

### 3.7 Routing & Protection

**Files to create:**

- `frontend/src/middleware.js` - Route protection middleware
- Protect librarian routes
- Protect user routes
- Redirect based on role

- `frontend/src/app/layout.jsx` - Root layout with providers
- Route protection logic

---

## Phase 4: Integration & Configuration

### 4.1 Environment Configuration

**Files to create:**

- Backend: `.env` or update `application.properties`
- Gateway: Environment variables for routes
- Frontend: `.env.local` for API Gateway URL

### 4.2 File Storage Setup

- Create `backend/uploads/books/` directory
- Configure static resource serving for images
- Setup image validation (size, format)

### 4.3 Email Configuration

- Configure SMTP settings in backend
- Create email templates for:
- Reservation confirmation
- Return reminder
- Account activation (if needed)

---

## Phase 5: Testing & Documentation

### 5.1 Backend Testing

- Unit tests for services
- Integration tests for controllers
- Security tests for JWT and RBAC

### 5.2 API Documentation

- Add Swagger/OpenAPI documentation
- Document all endpoints with examples

---

## Implementation Order & Dependencies

1. **Backend Database & Entities** (Foundation)
2. **Backend Security & JWT** (Critical infrastructure)
3. **Backend Services & Controllers** (Core functionality)
4. **API Gateway** (Routing layer)
5. **Frontend Auth** (User access)
6. **Frontend Features** (User experience)

---

## Key Technical Decisions

1. **JWT Token Structure:** Include user ID, email, and role in claims
2. **Password Hashing:** Use BCrypt with strength 10
3. **File Storage:** Local filesystem with organized directory structure
4. **Image Formats:** Support JPEG, PNG, WebP with max 5MB
5. **Reservation Periods:** Fixed options (7, 14, 21 days) selected by user
6. **Pagination:** 20 items per page for book listings
7. **CORS:** Configured at Gateway level for frontend origin

---

## Notes

- All dates should use UTC timestamps
- Implement soft delete where appropriate (books, categories)
- Add proper error messages and validation feedback
- Use consistent API response format across all endpoints
- Implement proper logging for debugging
- Add input sanitization to prevent SQL injection and XSS