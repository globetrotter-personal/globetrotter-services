# Copilot Instructions for Java and Spring Boot Projects

## Overview
These guidelines are designed to instruct GitHub Copilot to generate high-quality, idiomatic Java code using Spring Boot and related technologies. They ensure consistency, maintainability, and performance across projects.

---

## Code Style and Structure
- Write clean, efficient, and well-documented Java code.
- Use **Spring Boot best practices** and **project conventions**.
- Follow **RESTful API design patterns**.
- Use **camelCase** for method and variable names.
- Structure code into clear packages:
  - `controller`
  - `service`
  - `repository`
  - `model`
  - `configuration`

---

## Spring Boot Specifics
- Use Spring Boot starters for dependencies.
- Correctly apply annotations like:
  - `@SpringBootApplication`
  - `@RestController`
  - `@Service`
  - `@Repository`
- Leverage Spring Boot **auto-configuration**.
- Implement centralized exception handling using:
  - `@ControllerAdvice`
  - `@ExceptionHandler`

---

## Naming Conventions
- **PascalCase** for classes: `UserController`, `OrderService`
- **camelCase** for methods and variables: `getUserById`, `isValidOrder`
- **ALL_CAPS** for constants: `MAX_RETRY_ATTEMPTS`, `DEFAULT_PAGE_SIZE`

---

## Java & Spring Boot Features
- Use **Java 17+** features (records, sealed classes, pattern matching).
- Apply **Spring Boot 3.x** capabilities.
- Use **Spring Data JPA** for persistence.
- Validate inputs using Bean Validation: `@Valid`, `@NotNull`, custom validators.

---

## Configuration and Profiles
- Use `application.yml` or `application.properties`.
- Define **environment-specific configs** using **Spring Profiles**.
- Prefer `@ConfigurationProperties` for type-safe configuration.

---

## Dependency Injection & IoC
- Use **constructor injection** (not field injection).
- Let Spring manage bean lifecycles using **IoC container**.

---

## Testing Guidelines
- Use **JUnit 5** and `@SpringBootTest` for integration tests.
- Use **MockMvc** for controller tests.
- Use `@DataJpaTest` for repository tests.

---

## Performance & Scalability
- Use **Spring Cache** for caching.
- Apply `@Async` for non-blocking async processing.
- Optimize database performance via **indexing** and efficient queries.

---

## Security
- Use **Spring Security** for auth and access control.
- Store passwords securely using **BCrypt**.
- Configure **CORS** where applicable.

---

## Logging & Monitoring
- Use **SLF4J** with **Logback** for structured logging.
- Apply proper logging levels: `ERROR`, `WARN`, `INFO`, `DEBUG`.
- Monitor apps using **Spring Boot Actuator**.

---

## API Documentation
- Use **Springdoc OpenAPI** (Swagger) for generating API specs.

---

## Data Access & ORM
- Use **Spring Data JPA** and proper **entity relationships**.
- Apply cascading rules appropriately.
- Use **Flyway** or **Liquibase** for database migrations.

---

## Build & Deployment
- Use **Maven** for builds and dependency management.
- Define multiple profiles: `dev`, `test`, `prod`.
- Containerize using **Docker** when needed.

---

## Architectural Best Practices
- Follow **RESTful API design**:
  - Use correct HTTP methods and status codes.
- Apply **SOLID principles**.
- Ensure **high cohesion** and **low coupling**.
- Use **microservices** and **Spring WebFlux** for async/reactive needs if applicable.

---

## Usage Note for Copilot
When prompting GitHub Copilot:
- Be explicit about class roles (e.g., "create a `UserController` class with basic CRUD operations").
- Specify the layer (controller, service, repository) for better context.
- Include annotations and structure expectations in the prompt (e.g., "Use `@RestController`, `@Service`, and constructor-based dependency injection").

