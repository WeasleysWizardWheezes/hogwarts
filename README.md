# Fire Manager

Verwaltungssystem für Feuerwehr-Ressourcen.

## Tech Stack

| Layer    | Technologie                                      |
| -------- | ------------------------------------------------ |
| Frontend | React, TypeScript, Vite, Tailwind CSS, shadcn/ui |
| Backend  | Spring Boot 4, Java 25, PostgreSQL               |
| API      | REST (OpenAPI 3.1), openapi-fetch                |

## Projektstruktur

```
firemanager/
├── backend/     Spring Boot REST API
├── frontend/    React SPA
```

## Schnellstart

### Voraussetzungen

- Java 25+
- Node.js 20+
- Docker (für PostgreSQL)

### Backend starten

```bash
cd backend
docker compose up -d
./mvnw spring-boot:run
```

### Frontend starten

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

### API-Types aktualisieren

Bei Änderungen an Backend-Endpoints:

```bash
cd frontend
npm run api:generate
```

## API-Dokumentation

Swagger UI: http://localhost:8080/swagger-ui/index.html  
OpenAPI JSON: http://localhost:8080/v3/api-docs
