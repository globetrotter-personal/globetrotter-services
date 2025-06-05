# globetrotter-services

A modular Spring Boot 3.x Java 17+ service for integrating with multiple AI providers (OpenAI, DeepSeek, Anthropic, Gemini, Grok). Includes health check, secure secret management, Dockerization, and OpenAI completions endpoint.

## Project Structure
- `common/controller` — Shared REST controllers (e.g., health check)
- `common/service` — Shared business logic
- `openai/controller` — OpenAI-specific REST controllers
- `openai/service` — OpenAI integration logic
- `anthropic`, `deepseek`, `gemini`, `grok` — Placeholders for other AI providers
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
docker compose build
docker compose up -d
```
- The container will use the `OPENAI_API_KEY` environment variable.
- Example:
  ```sh
  OPENAI_API_KEY=sk-... docker compose up -d
  ```

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
- Modular structure supports adding more AI providers easily.
- Uses constructor injection, SLF4J logging, and robust error handling.
- See code for further details on extending to other providers.