# globetrotter-services

A modular Spring Boot 3.x Java 17+ service for integrating with multiple AI providers (OpenAI, DeepSeek, Anthropic, Gemini, Grok). Includes health check, secure secret management, Dockerization, and OpenAI completions endpoint.

## API Documentation

For detailed API documentation, see [API_REFERENCE.md](API_REFERENCE.md).

## Project Structure
- `common/controller` — Shared REST controllers (e.g., health check)
- `common/service` — Shared business logic
- `ai/openai/controller` — OpenAI-specific REST controllers
- `ai/openai/service` — OpenAI integration logic
- `ai/anthropic`, `ai/deepseek`, `ai/gemini`, `ai/grok` — Placeholders for other AI providers
- `model` — Domain models (future use)
- `repository` — Data access (future use)
- `configuration` — App configuration (future use)

## Endpoints
- `GET /common/health` — Health check (returns `OK` if running)
- `POST /openai/completion` — OpenAI completions endpoint
  - Request: `{ "prompt": "Say hello world" }`
  - Response: OpenAI completion result

## OpenAI API Key Setup
- The OpenAI API key is required for `/openai/completion`.
- **Never commit your API key to git!**
- The key can be provided in two ways:
  1. **Environment variable:**
     - Set `OPENAI_API_KEY` in your environment (recommended for Docker/prod)
  2. **Local properties file:**
     - Create `src/main/resources/application-local.properties`:
       ```properties
       openai.api.key=sk-...
       ```
     - This file is in `.gitignore` and will not be committed.

## Environment Variables and Secrets Management

- All sensitive configuration (API keys, client secrets) is now managed via environment variables using a `.env` file at the project root.
- **Do not commit your real `.env` file to git!**
- Instead, use the provided `.env-template` file as a reference for required variables. Copy it to `.env` and fill in your real values:

```sh
cp .env-template .env
# Edit .env and add your secrets
```

### Example `.env-template`
```
OPENAI_API_KEY=
AMADEUS_API_CLIENTID=
AMADEUS_API_CLIENTSECRET=
AMADEUS_API_BASEURL=https://test.api.amadeus.com
```

- The application no longer uses `application-local.properties` for secrets. That file has been removed and is now ignored by git.
- Docker Compose and Spring Boot will automatically load variables from `.env` if present.

## Running Locally
```sh
# 1. Set up your OpenAI API key (choose one method):
#    a) Export env var (preferred for Docker):
export OPENAI_API_KEY=sk-...
#    b) Or create src/main/resources/application-local.properties as above

# 2. Build and run
mvn clean package -DskipTests
java -jar target/globetrotter-services-0.0.1-SNAPSHOT.jar --spring.profiles.active=local

# Or run with Maven
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## Running with Docker
```sh
docker-compose up --build
```
- Make sure your `.env` file is present in the project root before running Docker Compose.
- The container will use the `OPENAI_API_KEY` environment variable.
- Example:
  ```sh
  OPENAI_API_KEY=sk-... docker compose up -d
  ```

## Docker Deployment
The application is containerized using Docker for easy deployment:

1. **Build the JAR file:**
   ```bash
   mvn clean install
   ```

2. **Build the Docker image:**
   ```bash
   docker compose build
   ```

3. **Run the Docker container:**
   ```bash
   docker compose up -d
   ```

4. **Check the health endpoint:**
   ```bash
   curl http://localhost:8080/common/health
   ```

5. **Test the OpenAI completion endpoint:**
   ```bash
   curl -X POST http://localhost:8080/openai/completion \
     -H "Content-Type: application/json" \
     -d '{"prompt": "Hello, how are you?", "maxTokens": 50}'
   ```

6. **Stop the Docker container:**
   ```bash
   docker compose down
   ```

## Configuration Files
- `application.yml` - Main configuration properties
- `application-local.properties` - Local development properties (not in git)
- `application-docker.properties` - Docker-specific properties

## SSL/TLS Configuration
The application includes special handling for SSL/TLS in Docker containers to properly connect to external APIs.

## Testing
```sh
mvn test
```

## Example Usage
```sh
curl -X POST http://localhost:8080/openai/completion \
  -H 'Content-Type: application/json' \
  -d '{"prompt": "Say hello world"}'
```

## Security Notes
- Never commit `application-local.properties` or your API keys.
- API keys are loaded securely via environment variable or local properties.

## Additional Notes
- Modular structure supports adding more AI providers easily under `ai/`.
- Uses constructor injection, SLF4J logging, and robust error handling.
- See code for further details on extending to other providers.

## Summary of Recent Changes
- All secrets moved from `application-local.properties` to `.env` (not tracked by git).
- `.env-template` added for onboarding and documentation.
- `application-local.properties` removed and added to `.gitignore`.
- Updated `.gitignore` to include `.env`, `.env-template`, and `application-local.properties`.
- Updated documentation for secure secret management and local/Docker usage.