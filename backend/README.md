# Fire Manager – Backend

Spring Boot REST API mit PostgreSQL.

## Voraussetzungen

- Java 25+
- Docker (für PostgreSQL)

## Setup

```bash
docker compose up -d
./mvnw spring-boot:run
```

Die Anwendung startet auf http://localhost:8080.

## API-Dokumentation

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

Das OpenAPI-Schema wird automatisch aus den `@RestController`-Klassen generiert.

## Tests

```bash
./mvnw verify
```

- Unit Tests + Integration Tests (Testcontainers)
- Code Coverage: JaCoCo (min. 80% Line Coverage)
- Mutation Tests: PIT (min. 70% Mutation Score)
- Checkstyle: `config/checkstyle/checkstyle.xml`

## Datenbank

PostgreSQL läuft via Docker Compose:

```bash
docker compose up -d     # starten
docker compose down      # stoppen
docker compose down -v   # stoppen + Daten löschen
```
