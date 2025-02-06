# Drink Water API

The Drink Water API provides a robust set of RESTful endpoints that power the core of the hydration monitoring platform.
This API enables client applications to track and record users' water intake, manage personalized hydration goals, and
access detailed consumption analytics. Built with Spring Boot and integrated with Keycloak for secure authentication,
the API delivers capabilities for user profile management, daily goal settings based on individual characteristics, and
generation of customized hydration reports.

The API offers a comprehensive suite of endpoints for:

- User profile management with secure authentication through Keycloak
- Daily water intake tracking with timestamp precision
- Customizable hydration goals based on user attributes
- Historical data retrieval with flexible date range filtering
- Analytics generation for tracking hydration patterns
- Notification preference management for client applications

This backend service is designed with scalability in mind, implementing caching strategies, database optimization, and
containerization support through Docker. The API follows REST best practices, providing clear documentation through
Swagger/OpenAPI specifications and implementing HATEOAS principles for improved API navigation and discovery.

## 🚀 Quick Start Example

Here's a complete example of how to interact with our Water Intake Tracking API:

### Authentication

First, obtain an access token from Keycloak:

```bash
curl -X POST 'http://localhost:8080/auth/realms/drinkwater/protocol/openid-connect/token' \
-H 'Content-Type: application/x-www-form-urlencoded' \
-d 'grant_type=password' \
-d 'client_id=drinkwaterapp' \
-d 'username=your-username' \
-d 'password=your-password'
```

You'll receive a response like this:

```json
{
  "access_token": "eyJhbGciOiJSUzI1...",
  "expires_in": 300,
  "token_type": "Bearer"
}
```

### Recording Water Intake

Record a new water intake entry:

```bash
curl -X POST 'http://localhost:8081/users/waterintakes' \
    -H 'Authorization: Bearer <YOUR_ACCESS_TOKEN>' \
    -H 'Content-Type: application/json' \
    -d '{
          "dateTimeUTC": "2024-01-26T14:30:00Z",
          "volume": 250,
          "volumeUnit": "ML"
        }'
```

Success response (HTTP 201):

```json
{
  "id": 1,
  "dateTimeUTC": "2024-01-26T14:30:00Z",
  "volume": 250,
  "volumeUnit": "ML"
}
```

### Searching Water Intake Records

Search for water intake records with filters:

```bash
curl -X GET 'http://localhost:8081/users/waterintakes?startDate=2024-01-26T00:00:00Z&endDate=2024-01-26T23:59:59Z&minVolume=200&maxVolume=1000&page=0&size=10&sortField=dateTimeUTC&sortDirection=DESC' \
-H 'Authorization: Bearer <YOUR_ACCESS_TOKEN>'
```

Success response (HTTP 200):

```json
{
  "content": [
    {
      "id": 1,
      "dateTimeUTC": "2024-01-26T14:30:00Z",
      "volume": 250,
      "volumeUnit": "ML"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "pageSize": 10,
  "pageNumber": 0,
  "first": true,
  "last": true
}
```

### Updating a Water Intake Record

Update an existing water intake entry:

```bash
curl -X PUT 'http://localhost:8081/users/waterintakes/1' \
    -H 'Authorization: Bearer <YOUR_ACCESS_TOKEN>' \
    -H 'Content-Type: application/json' \
    -d '{
          "dateTimeUTC": "2024-01-26T14:30:00Z",
          "volume": 300,
          "volumeUnit": "ML"
        }'
```

Success response (HTTP 200):

```json
{
  "id": 1,
  "dateTimeUTC": "2024-01-26T14:30:00Z",
  "volume": 300,
  "volumeUnit": "ML"
}
```

### Deleting a Water Intake Record

Delete a water intake entry:

```bash
curl -X DELETE 'http://localhost:8081/users/waterintakes/1' \
-H 'Authorization: Bearer <YOUR_ACCESS_TOKEN>'
```

Success response: HTTP 204 (No Content)

### Error Responses

The API uses standard HTTP status codes and returns detailed error messages:

```json
{
  "type": "https://www.drinkwater.com.br/validation-error",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation error occurred",
  "errors": [
    {
      "field": "volume",
      "message": "Volume must be greater than zero"
    }
  ]
}
```

Let me enhance the Technology Stack section with the specific versions and configuration details from your Dockerfile
and docker-compose.yml:

## 💻 Technology Stack

| Category                | Technology                    | Version       | Details                                                          |
|-------------------------|-------------------------------|---------------|------------------------------------------------------------------|
| Core                    | Java                          | 17            | Using Amazon Corretto JDK with custom runtime optimization       |
| Framework               | Spring Boot                   | 3.4.1         | Base framework for the application                               |
| Security                | Spring OAuth2 Resource Server | 3.4.1         | Handles OAuth2 resource protection                               |
| Authentication          | Keycloak                      | 26.0.7        | Handles authentication and authorization with PostgreSQL backend |
| Database                | PostgreSQL                    | 16-alpine     | Main application database and Keycloak database                  |
| Database (Test)         | H2 Database                   | Runtime       | In-memory database for testing                                   |
| Persistence             | Spring Data JPA               | 3.4.1         | Database access and ORM                                          |
| Validation              | Spring Boot Validation        | 3.4.1         | Request validation and error handling                            |
| Monitoring              | Spring Boot Actuator          | 3.4.1         | Application monitoring and metrics                               |
| Development             | Spring Boot DevTools          | 3.4.1         | Development productivity tools                                   |
| Build Tool              | Maven                         | 3.9.9         | Project build and dependency management                          |
| Containerization        | Docker                        | alpine 3.18.9 | Multi-stage build with optimized runtime image                   |
| Container Orchestration | Docker Compose                | 3.x           | Local development environment orchestration                      |

The application uses a multi-stage Docker build process that includes:

- Build stage using Maven and Amazon Corretto
- Custom JLink-optimized Java runtime
- Production-ready Alpine-based final image
- Containerized PostgreSQL databases for both application and Keycloak
- Pre-configured Keycloak server with health checks and PostgreSQL integration

## 🏗️ Architecture

The project follows a clean and modular architecture:

```
src/main/java/br/com/drinkwater/
├── config/                  # Application configurations
├── core/                    # Core components and utilities
├── exception/               # Global exception handling
├── hydrationtracking/       # Water intake tracking module
│   ├── controller/          # REST endpoints
│   ├── dto/                 # Data transfer objects
│   ├── mapper/              # Object mapping
│   ├── model/               # Domain entities
│   ├── repository/          # Data access
│   ├── service/             # Business logic
│   ├── specification/       # Query specifications
│   └── validation/          # Custom validators
├── usermanagement/          # User management module
│   ├── controller/          # User endpoints
│   ├── converter/           # JPA attribute converters for enums
│   ├── dto/                 # User DTOs
│   ├── mapper/              # User object mapping
│   ├── model/               # User domain entities
│   ├── repository/          # User data access
│   └── service/             # User business logic
└── validation/              # Shared validation components
```

Each module is self-contained with its own controllers, services, repositories, and domain models, following DDD principles and Clean Architecture patterns.

## ⚙️ Key Features

The project implements the following features:

### Security and Authentication
- OAuth2/OpenID Connect authentication using Keycloak
- Role-based access control
- JWT token validation
- Secure password management

### Water Intake Tracking
- Record and manage water intake entries
- Flexible volume units support
- Advanced filtering and sorting capabilities
- Pagination with customizable page sizes
- Duplicate entry prevention for same timestamp
- Time-range validations (max 31 days range)

### User Management
- Complete user profile management
- Physical characteristics tracking (height, weight, biological sex)
- Customizable alarm settings for hydration reminders
- Multiple unit system support (metric/imperial)

### Technical Features
- Comprehensive error handling with i18n support
- RFC 7807 Problem Details error responses
- Input validation with custom constraints
- Specification pattern for dynamic querying
- UTC time handling for global compatibility
- JPA optimized queries and specifications
- API pagination with sorting options

## 🚀 Getting Started

### Prerequisites

#### Required Software
- Java 17 (Amazon Corretto recommended)
- Docker (latest version)
- Docker Compose
- Maven 3.9.x

#### System Requirements
- Minimum 4GB RAM
- 2GB free disk space
- Ports available:
    - 8080 (Keycloak)
    - 8081 (Application)
    - 5432 (PostgreSQL)

#### Development Environment
- JDK 17 configured in PATH
- Docker engine running
- PostgreSQL client (optional, for direct DB access)
- API testing tool (Postman, cURL, etc.)

#### Database Setup
Handled automatically by Docker Compose, but requires:
- PostgreSQL 16 compatible system
- Two databases:
    - Main application: drink_water_db
    - Keycloak: security

### Installation Steps

  1. Clone the repository:
```bash
git clone git@github.com:eagle-head/drink-water-api.git
cd drink-water-api
```

  2. Start required services:
```bash
docker-compose up -d
```

  3. Build and run the application:
```bash
./mvnw clean install
./mvnw spring-boot:run
```

Services will be available at:
- Application: http://localhost:8081
- Keycloak: http://localhost:8080
    - Admin console: http://localhost:8080/admin
    - Username: admin
    - Password: password
- PostgreSQL: localhost:5432

## 🔐 Keycloak Configuration

Essential steps for Keycloak setup:

1. Access Keycloak admin console at `http://localhost:8080`
2. Create a new realm
3. Configure clients

## 🧪 Testing

This project includes comprehensive testing capabilities with unit tests, integration tests, code coverage reports, and mutation testing.

### Test Naming Pattern
We follow the `given_when_then` naming pattern for test methods, which helps create clear and descriptive test names:
- `given`: Initial context/preconditions
- `when`: Action or behavior being tested
- `then`: Expected outcome

Example: `givenValidUserData_whenCreateUser_thenReturnsUserResponseDTO()`

### Running Tests

#### Complete Test Suite
Run all unit and integration tests:
```bash
./mvnw clean verify
```

#### Unit Tests
Run only unit tests:
```bash
./mvnw clean test
```

#### Skip Integration Tests
Execute tests while skipping integration tests:
```bash
./mvnw clean verify -DskipITs=true
```

#### Skip Unit Tests
Execute tests while skipping unit tests:
```bash
./mvnw clean verify -Dsurefire.skip=true
```

### Code Coverage
Generate a detailed code coverage report using JaCoCo:
```bash
./mvnw clean test jacoco:report
```
After execution, you can find the HTML report in `target/site/jacoco/index.html`

### Mutation Testing
Run mutation testing with PIT to evaluate test suite effectiveness:
```bash
./mvnw test-compile org.pitest:pitest-maven:mutationCoverage
```
The mutation testing report will be available at `target/pit-reports/YYYYMMDDHHMI/index.html`

### Test Reports
Test reports are typically found under the `target` directory:
- JaCoCo: `target/site/jacoco/`
- PIT Mutation: `target/pit-reports/`
- Surefire: `target/surefire-reports/`

#### 🚨 Note: Exact paths may vary depending on your project configuration and build settings.

This documentation now includes complete information about the different types of testing available in the project, how to run them, and where to find the generated reports. The commands for code coverage and mutation testing have been integrated alongside the existing test commands, providing a comprehensive testing guide.

## 📝 API Documentation

Under construction

## 🛠️ Future Enhancements

* Unit and Integration Testing using Jacoco and Pitest (in progress), including testcontainers for integration tests

* Database Migration Management with Flyway for version control and automated schema updates

* API Documentation and Testing with OpenAPI/Swagger UI

* Event-Driven Architecture implementation using Apache Kafka for asynchronous communication

* Cache Management using:
  - Memcached for distributed memory caching
  - Apache Ignite for in-memory computing and caching
  - Hazelcast for distributed caching

* Metrics and Monitoring using:
  - Prometheus for metrics collection
  - Grafana for visualization
  - Micrometer for application metrics
  - OpenSearch + Logstash + Kibana for log aggregation
  - Jaeger or Zipkin for distributed tracing
  - Alert Manager for notifications

## 🔨 Development Practices

This project adheres to industry best practices:

- Clean Code principles
- SOLID design principles
- Comprehensive test coverage
- Security best practices

## 👨‍💻 Author

- LinkedIn: [Eduardo Kohn](https://www.linkedin.com/in/eduardo-kohn-56817b195/)
- GitHub: [Eduardo Kohn](https://github.com/eagle-head)
- Email: eduardokohn15 [at] gmail [dot] com

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

This means you can:
- Use it commercially
- Modify it
- Distribute it
- Use it privately
- Sublicense it

#### Just remember to include the original license and copyright notice in any copy of the project.

---

⭐️ If you found this project helpful, please consider giving it a star!