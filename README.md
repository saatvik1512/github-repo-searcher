# GitHub Repository Searcher

##  Overview
Spring Boot 4.x REST API application that searches GitHub repositories via the GitHub REST API,
stores results in PostgreSQL with efficient upsert logic, and provides filtered retrieval.

**Key Features:**
- ✅ Upsert logic (update existing repos instead of duplicates)
- ✅ Dynamic filtering & sorting
- ✅ Global exception handling with custom error responses
- ✅ Unit + integration tests (TDD approach)
- ✅ Swagger/OpenAPI documentation

##  Live Demo

| Resource | URL |
|----------|-----|
| **Deployed API** | [https://github-repo-searcher-xing.onrender.com/api/github/repositories](https://github-repo-searcher-xing.onrender.com/api/github/repositories) |
| **Swagger UI** | [https://github-repo-searcher-xing.onrender.com/swagger-ui/index.html](https://github-repo-searcher-xing.onrender.com/swagger-ui/index.html) |
| **OpenAPI JSON** | [https://github-repo-searcher-xing.onrender.com/v3/api-docs](https://github-repo-searcher-xing.onrender.com/v3/api-docs) |
| **GitHub Repository** | [https://github.com/saatvik1512/github-repo-searcher](https://github.com/saatvik1512/github-repo-searcher) |

>  **Note:** Hosted on Render Free Tier. First request after inactivity may take 30–60 seconds due to cold start.

##  Tech Stack

### Core
| Category | Technology | Version |
|----------|-----------|---------|
| Language | Java (built with JDK 24.0.1, compiled for Java 17) | 24.0.1 / target 17 |
| Framework | Spring Boot | 4.1.2 |
| Core Framework | Spring Framework | 7.0.10 |
| Web Server | Embedded Apache Tomcat | 11.0.27 |
| Data Access | Spring Data JPA | 4.1.2 |
| ORM | Hibernate ORM | 7.4.13.Final |
| Database | PostgreSQL | 17.5 |
| DB Driver | PostgreSQL JDBC | 42.7.14 |
| JSON | Jackson (`tools.jackson`) | 3.1.7 |
| API Docs | springdoc-openapi (webmvc-ui) | 3.2.0 |
| Boilerplate | Lombok | 1.18.48 |
| Build Tool | Apache Maven | 3.9.10 |

### Testing
| Category | Technology | Version |
|----------|-----------|---------|
| Test Framework | JUnit (Jupiter) | 6.0.3 |
| Mocking | Mockito (via Spring Boot test starter) | 5.23.0 |
| Test Database | H2 (in-memory) | 2.4.240 |
| Test Runner | Maven Surefire | 3.5.6 |

### Development Environment
| Item | Version |
|------|---------|
| OS | Windows 11 (amd64) |
| JDK | Oracle JDK 24.0.1 |
| Maven | 3.9.10 |
| DB Client | DBeaver |

> Library versions are resolved by the Spring Boot 4.1.2 BOM (except springdoc).
> Run `mvn dependency:tree` to see the full resolved list.

##  Quick Start

### Prerequisites
- JDK 17 or higher (developed and tested on JDK 24)
- Maven 3.9+
- PostgreSQL 15+ (developed on 17.5) or Docker

### 1. Database Setup
```bash
# Docker (Recommended)
docker run -d --name github-db \
  -e POSTGRES_DB=github_repo_db \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 postgres:17

# OR Manual SQL
CREATE DATABASE github_repo_db;
```
> **Note:** Docker is optional. If you already have PostgreSQL installed locally, 
> simply create the database manually and skip this step.

### 2. Environment Variables
Create a `.env` file in the project root (already in `.gitignore`):
```
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

`application.properties` reads it using Spring Boot's built-in config import:
```properties
spring.config.import=optional:file:.env[.properties]

spring.datasource.url=jdbc:postgresql://localhost:5432/github_repo_db
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
```

### 3. Run Application
```bash
mvn spring-boot:run
```

App will start at: `http://localhost:8080`

## API Endpoints

### POST /api/github/search
Fetch repositories from GitHub and save/update in DB.

**Request Body:**
```json
{
  "query": "spring boot",
  "language": "Java",
  "sort": "stars"
}
```

**Response (200 OK):**
```json
{
  "message": "Repositories fetched and saved successfully",
  "repositories": [
    {
      "id": 123456,
      "name": "spring-boot-example",
      "description": "An example repository for Spring Boot",
      "owner": "user123",
      "language": "Java",
      "stars": 450,
      "forks": 120,
      "lastUpdated": "2024-01-01T12:00:00Z"
    }
  ]
}
```

**Error (400 Bad Request):** returned when `query` is blank.

### GET /api/github/repositories
Retrieve stored repos with optional filters.

| Param | Type | Required | Default | Description |
|-------|------|----------|---------|-------------|
| language | String | No | - | Filter by programming language |
| minStars | Integer | No | - | Minimum stars threshold |
| sort | String | No | stars | Sort by: stars, forks, updated |

**Example:**
```
GET /api/github/repositories?language=Java&minStars=100&sort=forks
```

##  Testing

Integration tests run against an in-memory H2 database (profile `test`) and mock the
GitHub client, so no PostgreSQL or internet access is needed to run them.

### Run All Tests
```bash
mvn clean test
```

### Full Build
```bash
mvn clean install
```

### Postman Collection
Import `docs/GitHub_Repo_Searcher.postman_collection.json` into Postman.
All endpoints are pre-configured with sample requests and expected responses.

## 🏗️ Architecture & Design Patterns

| Pattern | Implementation |
|---------|---------------|
| **Layered Architecture** | Controller → Service → Repository separation |
| **DTO Pattern** | Request/Response DTOs separate from Entity |
| **Repository Pattern** | Spring Data JPA with custom upsert logic |
| **Global Exception Handler** | `@RestControllerAdvice` for consistent errors |
| **Mapper Pattern** | Dedicated `RepoMapper` utility class |
| **TDD** | Unit tests written alongside business logic |

### Key Design Decisions
- **Upsert via `githubId`**: Unique constraint prevents duplicates; existing records updated on re-search
- **In-memory Sorting**: Filters applied at DB level, sorting in service layer for flexibility
- **Null-safe Mapping**: Graceful handling of null owners/descriptions from GitHub API
- **Environment-based Config**: No hardcoded credentials; `.env` for local, env vars for deployment
- **Isolated Tests**: H2 + mocked `GitHubClient` keep tests fast and independent of external services

##  Project Structure
```
src/main/java/com/saatvik/project/
├── controller/          # REST endpoints
├── service/             # Business logic + GitHub client
├── entity/              # JPA entities
├── dto/                 # Request/Response DTOs
├── repository/          # Data access layer
├── exception/           # Global error handler
├── config/              # App configuration
└── utility/             # Mappers & helpers
```
