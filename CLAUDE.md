# Claude AI Assistant Guide

This document provides essential information for AI assistants working with the Drink Water API project.

## Project Overview

**Drink Water API** is a Spring Boot 3.4.8 application that provides RESTful endpoints for hydration tracking and user management. The system uses Keycloak for OAuth2 authentication and PostgreSQL for data persistence.

### Core Domains
- **Hydration Tracking**: Water intake recording, filtering, and analytics
- **User Management**: User profiles, physical characteristics, alarm settings

## Technology Stack

| Component | Technology | Version | Notes |
|-----------|------------|---------|-------|
| Java | Amazon Corretto JDK | 17 | Production runtime |
| Framework | Spring Boot | 3.4.8 | Core framework |
| Security | Spring OAuth2 Resource Server | 3.4.8 | JWT token validation |
| Authentication | Keycloak | 26.0.3 | Identity provider |
| Database | PostgreSQL | 16-alpine | Primary database |
| Test Database | H2 | Runtime | In-memory testing |
| Build Tool | Maven | 3.9.9 | Dependency management |
| Monitoring | Spring Boot Actuator + Prometheus | 3.4.8 | Metrics and health |

## Key Development Commands

### Docker Environment Management (Recommended)
The project includes a Makefile for intelligent Docker orchestration:

```bash
# Quick development start (minimal resources)
make dev-start              # Start only DB + Keycloak (~500MB RAM)
make app-run               # Run Spring Boot application

# Full stack with monitoring
make full-stack-start      # Start everything: app + monitoring (~2GB RAM)
make full-stack-stop       # Stop complete stack

# Individual stack management
make app-only              # Start only application dependencies
make monitor-only          # Start only monitoring stack (Prometheus/Grafana)

# Utility commands
make status                # Show status of all services
make health-check          # Check health of all services
make logs                  # Show logs from all services
make logs-app              # Show only application logs
make logs-monitor          # Show only monitoring logs
make clean                 # Clean all containers and volumes

# Development workflow
make dev-setup             # Complete setup: build + start dependencies
make test-integration      # Run integration tests with dependencies
make backup-db             # Backup application database

# Help
make help                  # Show all available commands
```

**Monitoring Stack Access Points:**
- **Grafana Dashboard**: http://localhost:3001 (admin/admin123)
- **Prometheus**: http://localhost:9090
- **AlertManager**: http://localhost:9093
- **Zipkin Tracing**: http://localhost:9411

### Manual Docker Commands (Alternative)
```bash
# Start dependencies manually
docker-compose -f deployment/docker-compose.yml up -d

# Start monitoring stack manually  
docker-compose -f monitoring/docker-compose-monitoring.yml up -d
```

### Building and Running
```bash
# Build the project
./mvnw clean install

# Run the application (after starting dependencies)
./mvnw spring-boot:run
```

### Testing
```bash
# Run all tests
./mvnw clean verify

# Unit tests only
./mvnw clean test

# Skip integration tests
./mvnw clean verify -DskipITs=true

# Code coverage report
./mvnw clean test jacoco:report

# Mutation testing
./mvnw test-compile org.pitest:pitest-maven:mutationCoverage
```

### Linting and Quality
```bash
# No specific linter configured - uses standard Java compilation
./mvnw compile

# Check for dependency updates
./mvnw versions:display-dependency-updates
```

## Project Structure

```
src/main/java/br/com/drinkwater/
├── config/                      # Spring configurations
│   ├── CorsConfig.java         # CORS settings
│   ├── SecurityConfig.java     # OAuth2 security
│   └── ValidationConfig.java   # Bean validation
├── core/                       # Shared utilities
│   ├── PageResponse.java       # Pagination wrapper
│   └── validation/             # Custom validators
├── exception/                  # Global exception handling
├── hydrationtracking/          # Water intake domain
│   ├── controller/            # REST endpoints
│   ├── dto/                   # Data transfer objects
│   ├── model/                 # JPA entities
│   ├── repository/            # Data access
│   ├── service/               # Business logic
│   └── validation/            # Domain validators
└── usermanagement/            # User profile domain
    ├── controller/            # User endpoints
    ├── dto/                   # User DTOs
    ├── model/                 # User entities
    ├── repository/            # User data access
    └── service/               # User business logic
```

## Important Patterns and Conventions

### Naming Conventions
- **Tests**: Use `given_when_then` pattern (e.g., `givenValidUser_whenCreateUser_thenReturnsUserDTO`)
- **DTOs**: Suffix with `DTO` (e.g., `WaterIntakeDTO`, `UserResponseDTO`)
- **Entities**: Plain names (e.g., `WaterIntake`, `User`)
- **Controllers**: Suffix with `Controller` (e.g., `WaterIntakeController`)
- **Services**: Suffix with `Service` (e.g., `WaterIntakeService`)

### Architecture Patterns
- **Clean Architecture**: Each domain module is self-contained
- **Specification Pattern**: Used for dynamic query building
- **DTO Pattern**: Separate DTOs for input/output operations
- **Mapper Pattern**: Object conversion between layers

### Security Implementation
- JWT tokens via Keycloak integration
- Resource server configuration in `SecurityConfig.java`
- User ID extraction from JWT claims (`sub` field)
- Role-based access control ready (not actively used)

### Database Design
- UTC timezone for all timestamps
- Enum converters for type safety
- Composite unique constraints for business rules
- Audit fields (creation/modification timestamps)

## Common Tasks

### Adding New Endpoints
1. Create DTO classes in appropriate domain
2. Add controller method with proper validation
3. Implement service layer business logic
4. Add repository methods if needed
5. Write comprehensive tests

### Adding Validation
- Use Bean Validation annotations (`@Valid`, `@NotNull`, etc.)
- Custom validators in `validation/` packages
- Global exception handling via `GlobalExceptionHandler`

### Database Changes
- Currently using `data.sql` for initial data
- Migration tool (Flyway) planned for future releases
- Test data reset via `reset.sql` in test resources

## API Endpoints

### Water Intake Management
- `POST /users/waterintakes` - Record water intake
- `GET /users/waterintakes` - Search with filters and pagination
- `PUT /users/waterintakes/{id}` - Update existing record
- `DELETE /users/waterintakes/{id}` - Delete record

### User Management
- `POST /users` - Create user profile
- `GET /users/me` - Get current user profile
- `PUT /users` - Update user profile
- `DELETE /users` - Delete user profile

## Testing Strategy

### Test Types
- **Unit Tests**: Individual class testing with mocks
- **Integration Tests**: Full Spring context with Testcontainers
- **Code Coverage**: JaCoCo reports (exclude config classes)
- **Mutation Testing**: PIT testing for test quality

### Test Configuration
- Separate application profiles for testing
- Testcontainers for PostgreSQL and Keycloak
- H2 database for lightweight unit tests
- RestAssured for API integration testing

## Environment Configuration

The application follows 12-Factor App principles with comprehensive environment variable support for configuration management.

### Spring Profiles

The application supports multiple Spring profiles for different environments:

- **`dev`**: Development profile with relaxed security and verbose logging
- **`staging`**: Staging profile with moderate security for testing
- **`prod`/`production`**: Production profile with maximum security and performance optimization
- **`default`**: Default profile with basic security settings

Activate profiles using:
```bash
# Single profile
SPRING_PROFILES_ACTIVE=dev

# Multiple profiles
SPRING_PROFILES_ACTIVE=dev,monitoring
```

### Environment Variables

The application uses environment variables extensively. Copy `.env.example` to `.env` and customize values:

```bash
cp .env.example .env
# Edit .env with your specific configuration
```

#### Core Application Settings
```bash
# Application identity
APP_NAME=drink-water-api
APP_VERSION=0.0.1-SNAPSHOT
APP_ENVIRONMENT=development
SERVER_PORT=8081

# Spring profile activation
SPRING_PROFILES_ACTIVE=dev
```

#### Database Configuration
```bash
# PostgreSQL connection
DATABASE_URL=jdbc:postgresql://localhost:5432/drink_water_db
DATABASE_USERNAME=username
DATABASE_PASSWORD=password
DATABASE_DRIVER=org.postgresql.Driver

# Connection pool settings
DATABASE_POOL_SIZE=10
DATABASE_MIN_IDLE=2
DATABASE_CONNECTION_TIMEOUT=30000

# JPA/Hibernate settings
JPA_HIBERNATE_DDL_AUTO=create-drop
JPA_SHOW_SQL=false
SQL_INIT_MODE=always
```

#### Keycloak OAuth2 Configuration
```bash
# Keycloak server
KEYCLOAK_URL=http://localhost:8080
KEYCLOAK_REALM=drinkwater
KEYCLOAK_CLIENT_ID=drinkwaterapp
KEYCLOAK_USERNAME=admin-drinkwater
KEYCLOAK_PASSWORD=admin

# JWT settings
KEYCLOAK_ISSUER_URI=http://localhost:8080/realms/drinkwater
KEYCLOAK_JWK_SET_URI=http://localhost:8080/realms/drinkwater/protocol/openid-connect/certs
JWT_CACHE_DURATION=PT5M
```

#### Security Configuration
```bash
# CORS settings
CORS_ALLOWED_ORIGIN=http://localhost:3000
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080
CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,OPTIONS
CORS_ALLOW_CREDENTIALS=true

# Rate limiting (production)
SECURITY_RATE_LIMITING_ENABLED=true
SECURITY_RATE_LIMIT_RPM=60
SECURITY_RATE_LIMIT_BURST=10
```

#### Actuator & Monitoring Configuration
```bash
# Actuator endpoint exposure (security-sensitive!)
ACTUATOR_ENDPOINTS=health,info,metrics,prometheus
ACTUATOR_BASE_PATH=/actuator

# Health endpoint settings
ACTUATOR_HEALTH_SHOW_DETAILS=when-authorized
ACTUATOR_HEALTH_SHOW_COMPONENTS=always

# Prometheus metrics
PROMETHEUS_METRICS_ENABLED=true
PROMETHEUS_METRICS_STEP=10s

# Distributed tracing
TRACING_ENABLED=false
TRACING_SAMPLING_RATE=0.1
ZIPKIN_ENDPOINT=http://localhost:9411/api/v2/spans
```

#### Logging Configuration
```bash
# Global log levels
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_APP=INFO
LOGGING_LEVEL_SECURITY=WARN
LOGGING_LEVEL_OAUTH2=WARN

# Development logging (dev profile)
LOGGING_LEVEL_SQL=DEBUG
LOGGING_LEVEL_SQL_PARAMS=TRACE
LOGGING_LEVEL_SECURITY=DEBUG
```

#### Docker Compose Variables
```bash
# Database containers
DATABASE_NAME=drink_water_db
KEYCLOAK_DATABASE_NAME=security
KEYCLOAK_DATABASE_USERNAME=admin
KEYCLOAK_DATABASE_PASSWORD=admin

# Monitoring stack
PROMETHEUS_VERSION=v2.47.0
PROMETHEUS_PORT=9090
GRAFANA_VERSION=10.1.0
GRAFANA_PORT=3001
GRAFANA_ADMIN_USER=admin
GRAFANA_ADMIN_PASSWORD=admin123
ZIPKIN_VERSION=2.24
ZIPKIN_PORT=9411
```

### Security Considerations

#### Environment-Specific Security

| Environment | Security Level | Actuator Endpoints | Auth Requirements |
|-------------|---------------|-------------------|-------------------|
| **Development** | Relaxed | All available | Optional for most |
| **Staging** | Moderate | Limited set | OAuth2 required |
| **Production** | Maximum | Minimal set | OAuth2 + IP restrictions |

#### Critical Security Variables

**NEVER commit these to version control:**
- `DATABASE_PASSWORD`
- `KEYCLOAK_PASSWORD`
- `KEYCLOAK_ADMIN_PASSWORD`
- `JWT_SIGNING_KEY` (if using custom keys)

**Production-specific requirements:**
- Use HTTPS URLs for all Keycloak endpoints
- Restrict CORS origins to actual frontend domains
- Limit actuator endpoints to `health,info,metrics,prometheus`
- Enable IP whitelisting for monitoring endpoints
- Use strong, unique passwords
- Consider secrets management solutions (Kubernetes secrets, AWS Secrets Manager)

### Configuration Files Structure

```
src/main/resources/
├── application.yml          # Default configuration with env vars
├── application-dev.yml      # Development profile overrides
├── application-prod.yml     # Production profile overrides
├── application-test.yml     # Test profile overrides
└── application-it.yml       # Integration test overrides
```

### Environment Variable Validation

The application validates critical environment variables at startup:
- Database connection parameters
- Keycloak connectivity
- Required security settings in production

### Application Ports
- Application: 8081 (configurable via `SERVER_PORT`)
- Keycloak: 8080 (configurable via `KEYCLOAK_PORT`)
- PostgreSQL: 5432 (standard)
- Monitoring stack: Various ports (see monitoring section)

### Docker Environment Setup

1. **Copy environment template:**
   ```bash
   cp .env.example .env
   ```

2. **Edit configuration for your environment:**
   ```bash
   # For local development
   SPRING_PROFILES_ACTIVE=dev
   DATABASE_URL=jdbc:postgresql://localhost:5432/drink_water_db
   
   # For Docker development  
   DATABASE_URL=jdbc:postgresql://postgres:5432/drink_water_db
   ```

3. **Start with Docker Compose:**
   ```bash
   # Uses .env file automatically
   make dev-start
   ```

## Error Handling

The application implements RFC 7807 Problem Details for consistent error responses:

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

## Monitoring and Observability

- **Health Checks**: `/actuator/health`
- **Metrics**: `/actuator/prometheus` (Prometheus format)
- **Info**: `/actuator/info`
- **Custom Metrics**: Micrometer integration ready

## Development Tips

### When Working with This Project:
1. **Start with Makefile**: Use `make dev-start` instead of manual docker commands
2. **Resource Management**: Use `make dev-start` for development (lighter) vs `make full-stack-start` for full debugging
3. Use the proper test commands for different testing scenarios
4. Follow the established naming conventions
5. Add comprehensive tests for new features
6. Consider security implications (JWT validation, data access)
7. Use UTC for all datetime operations
8. Implement proper error handling with meaningful messages

### Recommended Daily Workflow:
```bash
# Morning: Start development
make dev-start              # Quick start (30 seconds)
make app-run               # Run application

# Need debugging/monitoring?
make monitor-only          # Add monitoring stack

# End of day
make dev-stop              # Clean shutdown
```

### Common Issues:
- **Port conflicts**: Ensure 8080, 8081, 5432 are available
- **Docker issues**: Restart Docker services if containers fail
- **Test failures**: Check Testcontainers configuration
- **Authentication**: Verify Keycloak realm configuration

## Future Enhancements

Planned improvements include:
- Database migration management with Flyway
- OpenAPI/Swagger documentation
- Event-driven architecture with Kafka
- Advanced caching strategies
- Enhanced monitoring with Grafana/ELK stack

---

This guide should help AI assistants understand the project structure, conventions, and important considerations when working with the Drink Water API codebase.