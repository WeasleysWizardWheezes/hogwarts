# Firemanager - Dienst-, Termin- & Einsatzplanung

Ein Spring Boot-basiertes System für die Verwaltung von Diensten, Terminen und Einsätzen für Feuerwehr-Organisationseinheiten.

## Features

### 1. Dienst- und Übungsplanung
- Erstellen, Bearbeiten und Löschen von Diensten und Übungen
- Zuordnung zu Kalendern und Organisationseinheiten
- Wiederkehrende Dienste/Übungen (täglich, wöchentlich, monatlich, jährlich)

### 2. Kalenderfunktionen
- Kalenderverwaltung (CRUD)
- Freigabe von Kalendern (öffentlich, privat, für bestimmte Benutzer)
- Verschiedene Ansichten (Tages-, Wochen-, Monats-, Jahresansicht)

### 3. Terminverwaltung
- Terminverwaltung für Organisationseinheiten
- Zuordnung zu Kalendern
- Wiederkehrende Termine

### 4. Teilnahmeverwaltung
- Teilnehmer hinzufügen/entfernen
- Teilnahme bestätigen/absagen
- Teilnahme-Status (zugesagt, abgesagt, unentschieden)
- Erinnerungen an Teilnehmer

### 5. Erinnerungsfunktionen
- Erinnerungen für Termine, Dienste und Übungen
- Versand per E-Mail, SMS oder Push-Benachrichtigung
- Zeitgesteuerte Erinnerungen (z. B. 1 Stunde, 1 Tag, 1 Woche vor dem Termin)
- Wiederkehrende Erinnerungen

### 6. Alarm- und Einsatzplanung
- Alarm- und Einsatzverwaltung
- Zuordnung zu Organisationseinheiten und Kalendern
- Wiederkehrende Alarme/Einsätze

### 7. Automatische Zuordnung
- Automatische Zuordnung von Terminen und Einsätzen basierend auf Regeln
- Regelverwaltung für automatische Zuordnung
- Manuelle Überschreibung möglich

## Technologien

- **Spring Boot 4.1.0**
- **Java 25**
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok**
- **JUnit 5**
- **Mockito**
- **Testcontainers**
- **Docker**

## Architektur

### Schichten

1. **Controller-Schicht**: REST-API Endpunkte
2. **Service-Schicht**: Business-Logik
3. **Repository-Schicht**: Datenzugriff
4. **Model-Schicht**: Entitäten und DTOs

### Paketstruktur

```
de.thkoeln.ccq.firemanager
├── controller          # REST Controller
├── dto                # Data Transfer Objects
├── model              # JPA Entitäten
├── repository         # Spring Data JPA Repositories
└── service            # Business Services
```

## API-Dokumentation

### Basis-URL

`http://localhost:8080/api`

### Endpunkte

#### Organisationseinheiten

- `GET /organization-units` - Alle Organisationseinheiten abrufen
- `GET /organization-units/{id}` - Organisationseinheit abrufen
- `POST /organization-units` - Organisationseinheit erstellen
- `PUT /organization-units/{id}` - Organisationseinheit aktualisieren
- `DELETE /organization-units/{id}` - Organisationseinheit löschen

#### Kalender

- `GET /calendars` - Alle Kalender abrufen
- `GET /calendars/{id}` - Kalender abrufen
- `GET /calendars/organization-unit/{orgUnitId}` - Kalender nach Organisationseinheit
- `POST /calendars` - Kalender erstellen
- `PUT /calendars/{id}` - Kalender aktualisieren
- `DELETE /calendars/{id}` - Kalender löschen
- `POST /calendars/{id}/share` - Kalender freigeben

#### Dienste

- `GET /services` - Alle Dienste abrufen
- `GET /services/{id}` - Dienst abrufen
- `GET /services/organization-unit/{orgUnitId}` - Dienste nach Organisationseinheit
- `POST /services` - Dienst erstellen
- `PUT /services/{id}` - Dienst aktualisieren
- `DELETE /services/{id}` - Dienst löschen

#### Übungen

- `GET /exercises` - Alle Übungen abrufen
- `GET /exercises/{id}` - Übung abrufen
- `GET /exercises/organization-unit/{orgUnitId}` - Übungen nach Organisationseinheit
- `POST /exercises` - Übung erstellen
- `PUT /exercises/{id}` - Übung aktualisieren
- `DELETE /exercises/{id}` - Übung löschen

#### Termine

- `GET /appointments` - Alle Termine abrufen
- `GET /appointments/{id}` - Termin abrufen
- `GET /appointments/organization-unit/{orgUnitId}` - Termine nach Organisationseinheit
- `GET /appointments/calendar/{calendarId}` - Termine nach Kalender
- `POST /appointments` - Termin erstellen
- `PUT /appointments/{id}` - Termin aktualisieren
- `DELETE /appointments/{id}` - Termin löschen

#### Teilnehmerverwaltung

- `POST /appointments/{appointmentId}/participants` - Teilnehmer hinzufügen
- `DELETE /appointments/{appointmentId}/participants` - Teilnehmer entfernen
- `POST /appointments/{appointmentId}/participants/{userId}/accept` - Teilnahme bestätigen
- `POST /appointments/{appointmentId}/participants/{userId}/decline` - Teilnahme absagen
- `GET /appointments/{appointmentId}/participants/{userId}/status` - Teilnahme-Status abrufen

#### Erinnerungen

- `GET /reminders` - Alle Erinnerungen abrufen
- `GET /reminders/{id}` - Erinnerung abrufen
- `GET /reminders/appointment/{appointmentId}` - Erinnerungen nach Termin
- `GET /reminders/user/{userId}` - Erinnerungen nach Benutzer
- `POST /reminders` - Erinnerung erstellen
- `PUT /reminders/{id}` - Erinnerung aktualisieren
- `DELETE /reminders/{id}` - Erinnerung löschen

#### Alarme

- `GET /alarms` - Alle Alarme abrufen
- `GET /alarms/{id}` - Alarm abrufen
- `GET /alarms/organization-unit/{orgUnitId}` - Alarme nach Organisationseinheit
- `GET /alarms/calendar/{calendarId}` - Alarme nach Kalender
- `POST /alarms` - Alarm erstellen
- `PUT /alarms/{id}` - Alarm aktualisieren
- `DELETE /alarms/{id}` - Alarm löschen

#### Regeln

- `GET /rules` - Alle Regeln abrufen
- `GET /rules/{id}` - Regel abrufen
- `POST /rules` - Regel erstellen
- `PUT /rules/{id}` - Regel aktualisieren
- `DELETE /rules/{id}` - Regel löschen

#### Automatische Zuordnung

- `POST /auto-assign` - Automatische Zuordnung ausführen

## Datenbank

### PostgreSQL

Die Anwendung verwendet PostgreSQL als Datenbank. Die Konfiguration befindet sich in `src/main/resources/application.properties`.

### Entitäten

- `OrganizationUnit` - Organisationseinheit
- `Calendar` - Kalender
- `User` - Benutzer
- `Service` - Dienst
- `Exercise` - Übung
- `Appointment` - Termin
- `Alarm` - Alarm
- `Reminder` - Erinnerung
- `Rule` - Regel für automatische Zuordnung

## Entwicklung

### Voraussetzungen

- Java 25
- Maven 3.9+
- Docker (für PostgreSQL)
- PostgreSQL 16+

### Aufbau und Ausführung

1. **Datenbank starten**:

```bash
docker-compose up -d postgres
```

2. **Anwendung bauen**:

```bash
mvn clean package
```

3. **Anwendung starten**:

```bash
mvn spring-boot:run
```

4. **Tests ausführen**:

```bash
mvn test
```

5. **Integrationstests ausführen**:

```bash
mvn verify
```

### Docker

Die Anwendung kann auch mit Docker ausgeführt werden:

```bash
docker-compose up --build
```

## Konfiguration

Die Hauptkonfiguration befindet sich in `src/main/resources/application.properties`:

```properties
# Datenbank
spring.datasource.url=jdbc:postgresql://localhost:5432/firemanager
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Server
server.port=8080
```

## Qualitätssicherung

### Checkstyle

Das Projekt verwendet Checkstyle für Code-Qualität. Die Konfiguration befindet sich in `config/checkstyle/checkstyle.xml`.

### Testabdeckung

- **JaCoCo**: Mindestens 80% Codeabdeckung
- **PIT**: Mindestens 70% Mutationstestabdeckung

### Quality Gates

- Checkstyle: Wird während des Builds ausgeführt
- JaCoCo: Wird während der Testphase ausgeführt
- PIT: Wird während der Verifizierungsphase ausgeführt

## Beitragende

- [HermioneGrangerAgent](https://github.com/HermioneGrangerAgent)

## Lizenz

Dieses Projekt ist lizenziert unter der MIT-Lizenz.

## Support

Für Fragen oder Probleme bitte ein Issue im GitHub-Repository erstellen.
