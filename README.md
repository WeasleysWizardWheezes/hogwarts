# Firemanager

Spring Boot Anwendung mit PostgreSQL-Datenbank.

## Voraussetzungen

- Java 25
- Maven
- Docker & Docker Compose

## Lokale Entwicklung

1. Datenbank starten:
   ```bash
   docker compose up -d
   ```

2. Anwendung starten:
   ```bash
   ./mvnw spring-boot:run
   ```

Die Anwendung läuft auf `http://localhost:8080`.
Die PostgreSQL-Datenbank läuft auf Port `5432` (Datenbank: `firemanager`, User: `postgres`, Passwort: `postgres`).
