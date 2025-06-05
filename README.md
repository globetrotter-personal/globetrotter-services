# globetrotter-services

A Spring Boot 3.x Java 17+ service with a sample health check endpoint.

## Structure
- `controller` — REST controllers
- `service` — Business logic
- `model` — Domain models (not used in health check)
- `repository` — Data access (not used in health check)
- `configuration` — App configuration (not used in health check)

## Health Check
- `GET /health` — Returns `OK` if the service is running.

## Run
```sh
mvn spring-boot:run
```

## Test
```sh
mvn test
```