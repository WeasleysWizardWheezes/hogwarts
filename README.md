# Fire Manager

Verwaltungssystem für Feuerwehr-Ressourcen.

## Tech Stack

| Layer    | Technologie                                      |
|----------|--------------------------------------------------|
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

### Zugriffsrechte

Wichtig für die Sicherheit: Die Dateien .npmrc und .env sind aus Sicherheitsgründen fest in der .gitignore hinterlegt
und werden nicht ins Repository übertragen. Da wir private Pakete aus der @weasleyswizardwheezes-Organisation nutzen,
müssen diese Dateien vor dem ersten Start lokal angelegt werden.

#### 1. GitHub Personal Access Token (PAT) erstellen

Um die privaten npm-Pakete herunterladen zu können, benötigst du ein GitHub-Token:

1. Gehe auf GitHub zu Settings -> Developer Settings -> Personal Access Tokens (classic).
2. Generiere ein neues Token mit dem Scope: read:packages.

#### 2. .npmrc einrichten

Erstelle im frontend/-Ordner eine Datei namens .npmrc mit diesem Inhalt:

```
@weasleyswizardwheezes:registry=https://npm.pkg.github.com/weasleyswizardwheezes
//npm.pkg.github.com/weasleyswizardwheezes/:_authToken=YOUR_GITHUB_TOKEN
```

#### 3. .env einrichten

Erstelle im Wurzelverzeichnis des Projekts eine .env-Datei für lokale Umgebungsvariablen:

```
GITHUB_TOKEN=DEIN_GITHUB_TOKEN
GITHUB_ACTOR=DEIN_GITHUB_BENUTZERNAME
```

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

## API-Dokumentation

Swagger UI: http://localhost:8080/swagger-ui/index.html  
OpenAPI JSON: http://localhost:8080/v3/api-docs
