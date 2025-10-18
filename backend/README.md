# E-commerce Backend API

Spring Boot backend API for the e-commerce application using PostgreSQL database.

## 🚀 Features

- **User Management**: Complete user CRUD operations with role-based access
- **Product Management**: Product catalog with variants, images, and categories
- **Order Management**: Order processing with status tracking
- **Cart System**: Shopping cart functionality
- **Review System**: Product reviews and ratings
- **Admin Functions**: User banning, role management, audit logging

## 🛠️ Tech Stack

- **Java 21**
- **Spring Boot 3.5.6**
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok**
- **Maven**

## 📋 Prerequisites

- Java 21+
- Maven 3.6+
- PostgreSQL 12+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## ⚙️ Configuration

### Environment Variables

Create a `.env` file or set these environment variables:

```bash
DB_URL=jdbc:postgresql://localhost:5432/ecommerce_db
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

### Database Setup

1. Create a PostgreSQL database:

```sql
CREATE DATABASE ecommerce_db;
```

2. The application will automatically create tables on first run (using `spring.jpa.hibernate.ddl-auto=update`)

## 🚀 Running the Application

### Using Maven

```bash
# Navigate to backend directory
cd backend

# Run the application
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run
```

### Using IDE

1. Open the project in your IDE
2. Run `BackendApplication.java` as a Java application

## 📚 API Endpoints

### Base URL

```
http://localhost:8080/api
```

### User Endpoints

| Method | Endpoint               | Description               |
| ------ | ---------------------- | ------------------------- |
| GET    | `/users`               | Get all users (paginated) |
| GET    | `/users/{id}`          | Get user by ID            |
| GET    | `/users/email/{email}` | Get user by email         |
| GET    | `/users/role/{role}`   | Get users by role         |
| POST   | `/users`               | Create new user           |
| PUT    | `/users/{id}`          | Update user               |
| DELETE | `/users/{id}`          | Delete user               |
| POST   | `/users/{id}/ban`      | Ban user                  |
| POST   | `/users/{id}/unban`    | Unban user                |
| PUT    | `/users/{id}/role`     | Update user role          |

### Product Endpoints

| Method | Endpoint                          | Description                  |
| ------ | --------------------------------- | ---------------------------- |
| GET    | `/products`                       | Get all products (paginated) |
| GET    | `/products/published`             | Get published products       |
| GET    | `/products/{id}`                  | Get product by ID            |
| GET    | `/products/slug/{slug}`           | Get product by slug          |
| GET    | `/products/brand/{brandId}`       | Get products by brand        |
| GET    | `/products/category/{categoryId}` | Get products by category     |
| GET    | `/products/search?keyword=...`    | Search products              |
| POST   | `/products`                       | Create new product           |
| PUT    | `/products/{id}`                  | Update product               |
| DELETE | `/products/{id}`                  | Delete product               |
| POST   | `/products/{id}/publish`          | Publish product              |
| POST   | `/products/{id}/archive`          | Archive product              |

## 🗄️ Database Schema

The application uses the following main entities:

- **User**: User accounts with roles and authentication
- **Product**: Product catalog with variants and images
- **Category**: Product categories with hierarchy
- **Brand**: Product brands
- **Order**: Order management with status tracking
- **Cart**: Shopping cart functionality
- **Review**: Product reviews and ratings
- **Address**: User shipping addresses

## 🔧 Configuration Options

### Database Configuration

```properties
# Connection settings
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Connection pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

### JPA Configuration

```properties
# Schema management
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### CORS Configuration

```properties
# Frontend integration
spring.web.cors.allowed-origins=http://localhost:3000
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
```

## 🧪 Testing

### Running Tests

Run all tests using Maven:

```bash
# Run all tests
./mvnw test

# Run tests with coverage report
./mvnw test jacoco:report

# Run specific test class
./mvnw test -Dtest=UserControllerTest

# Run tests in specific package
./mvnw test -Dtest="shopco.backend.controller.*"
```

### Test Structure

The test suite includes:

#### **Unit Tests**

- **Controller Tests**: Test REST endpoints with mocked services
- **Service Tests**: Test business logic with mocked repositories
- **Repository Tests**: Test data access layer (if needed)

#### **Integration Tests**

- **Full Stack Tests**: Test complete request-response cycle
- **Database Tests**: Test with real H2 in-memory database
- **API Tests**: Test actual HTTP requests and responses

### Test Categories

| Test Type         | Location                                    | Purpose                                   |
| ----------------- | ------------------------------------------- | ----------------------------------------- |
| Unit Tests        | `src/test/java/shopco/backend/controller/`  | Test controllers with mocked dependencies |
| Unit Tests        | `src/test/java/shopco/backend/service/`     | Test service layer business logic         |
| Integration Tests | `src/test/java/shopco/backend/integration/` | Test full application flow                |

### Test Configuration

Tests use H2 in-memory database with the following configuration:

```properties
# Test database
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
spring.test.database.replace=none
```

### Running Specific Test Types

```bash
# Run only unit tests
./mvnw test -Dtest="*Test"

# Run only integration tests
./mvnw test -Dtest="*IntegrationTest"

# Run controller tests only
./mvnw test -Dtest="*ControllerTest"

# Run service tests only
./mvnw test -Dtest="*ServiceTest"
```

### Test Coverage

The project includes comprehensive test coverage for:

- ✅ **User Management**: CRUD operations, role management, banning
- ✅ **Product Management**: CRUD operations, status changes, search
- ✅ **API Endpoints**: All REST endpoints with various scenarios
- ✅ **Error Handling**: Exception scenarios and edge cases
- ✅ **Database Operations**: Create, read, update, delete operations
- ✅ **Validation**: Input validation and business rules

### Test Data

Tests use:

- **H2 In-Memory Database**: Fast, isolated test environment
- **MockMvc**: For testing HTTP endpoints
- **@Transactional**: Automatic rollback after each test
- **Test Fixtures**: Reusable test data setup

## 📝 Development Notes

### Entity Relationships

- Users can have multiple addresses, orders, and reviews
- Products belong to categories and brands
- Orders contain multiple order items
- Products can have multiple variants and images

### JSON Fields

Some entities use JSON fields for flexible data storage:

- `ProductVariant.attributes`: Variant attributes (color, size, etc.)
- `Order.shippingAddress`: Shipping address snapshot
- `Payment.metadata`: Payment provider metadata

### Role-Based Access

- Users have roles: `user`, `admin`
- Admin users can manage other users and products
- Regular users can only access their own data

## 🚀 Deployment

### Production Configuration

1. Update `application.properties` for production
2. Set `spring.jpa.hibernate.ddl-auto=none`
3. Configure proper database connection pooling
4. Set up proper logging levels
5. Configure CORS for production domains

### Docker (Optional)

```dockerfile
FROM openjdk:21-jdk-slim
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.
