---
name: api-richtlinien
description: Nutze diesen Skill beim Entwerfen, Prüfen oder Implementieren von REST-API-Endpunkten, DTOs, Request-/Response-Contracts, HTTP-Statuscodes, Validierung, Fehlerformaten, Pagination, Filtering, Sorting, Versionierung oder API-Dokumentation.
---

# API-Richtlinien

Dieser Skill definiert allgemeine Regeln für konsistente, wartbare und verständliche REST-APIs.

## Grundprinzipien

* APIs werden als stabile Verträge zwischen Client und Server behandelt.
* Pfade beschreiben Ressourcen, keine Aktionen.
* HTTP-Methoden beschreiben die gewünschte Operation.
* Controller bleiben dünn und enthalten keine Fachlogik.
* JPA-Entities oder interne Domain-Objekte werden nicht direkt über die API zurückgegeben.
* Request- und Response-DTOs werden sauber getrennt.

---

## URL-Design

Alle API-Pfade folgen diesem Muster:

```http
/api/v1/{resource}
```

Regeln:

* Ressourcen im Plural benennen.
* Lowercase verwenden.
* Bei mehreren Wörtern kebab-case verwenden.
* Keine Verben in Pfaden verwenden.
* IDs nur für konkrete Einzelressourcen verwenden.
* Filter, Sortierung und Pagination über Query-Parameter abbilden.

Gute Beispiele:

```http
GET    /api/v1/equipment
POST   /api/v1/equipment
GET    /api/v1/equipment/{equipmentId}
PUT    /api/v1/equipment/{equipmentId}
PATCH  /api/v1/equipment/{equipmentId}
DELETE /api/v1/equipment/{equipmentId}

GET    /api/v1/vehicles
GET    /api/v1/vehicles/{vehicleId}
GET    /api/v1/locations/{locationId}/equipment
```

Schlechte Beispiele:

```http
GET  /api/v1/getEquipment
POST /api/v1/createEquipment
GET  /api/v1/equipmentList
POST /api/v1/deleteEquipment
```

---

## HTTP-Methoden

* `GET` zum Lesen von Ressourcen.
* `POST` zum Erstellen neuer Ressourcen.
* `PUT` zum vollständigen Ersetzen einer Ressource.
* `PATCH` für Teiländerungen.
* `DELETE` zum Löschen einer Ressource.

Regeln:

* `GET` darf keine Zustandsänderung auslösen.
* `PUT` ist idempotent und erwartet den vollständigen neuen Zustand.
* `PATCH` ändert nur einzelne Felder.
* `DELETE` gibt bei erfolgreicher Löschung in der Regel `204 No Content` zurück.

---

## Statuscodes

Erfolgsfälle:

* `200 OK` – Erfolg mit Response-Body
* `201 Created` – Ressource erstellt, möglichst mit `Location`-Header
* `204 No Content` – Erfolg ohne Response-Body

Clientfehler:

* `400 Bad Request` – ungültiger Request oder Validierungsfehler
* `401 Unauthorized` – Authentifizierung fehlt oder ist ungültig
* `403 Forbidden` – Nutzer ist authentifiziert, aber nicht berechtigt
* `404 Not Found` – Ressource nicht gefunden
* `409 Conflict` – Konflikt mit aktuellem Systemzustand

Serverfehler:

* `500 Internal Server Error` – unerwarteter Serverfehler

---

## Fehler-Responses

Alle Fehler-Responses verwenden ein einheitliches JSON-Format:

```json
{
  "error": "VALIDATION_FAILED",
  "message": "Beschreibung",
  "details": []
}
```

Für Feldfehler:

```json
{
  "error": "VALIDATION_FAILED",
  "message": "Die Anfrage enthält ungültige Felder.",
  "details": [
    {
      "field": "name",
      "message": "Der Name darf nicht leer sein."
    }
  ]
}
```

Regeln:

* Keine Stacktraces zurückgeben.
* Keine SQL-Fehler zurückgeben.
* Keine internen Klassennamen zurückgeben.
* `error` ist maschinenlesbar und stabil.
* `message` ist für Menschen verständlich.
* `details` enthält optionale strukturierte Zusatzinformationen.

---

## DTO-Regeln

Request- und Response-Objekte werden getrennt.

Beispiel:

```java
public record CreateEquipmentRequest(
    String name,
    String serialNumber
) {}
```

```java
public record EquipmentResponse(
    UUID id,
    String name,
    String serialNumber
) {}
```

Regeln:

* Keine Entity als Request- oder Response-Body verwenden.
* Request-DTOs enthalten nur Felder, die der Client setzen darf.
* Response-DTOs enthalten nur Felder, die der Client sehen darf.
* Server-Felder wie `id`, `createdAt` oder `updatedAt` werden nicht über Create-Requests gesetzt.
* Für unterschiedliche Use Cases dürfen unterschiedliche DTOs verwendet werden.

---

## Validierung

Technische Validierung:

* Pflichtfelder
* String-Längen
* Zahlenbereiche
* gültige UUIDs
* gültige Enum-Werte
* gültige Datumsformate

Fachliche Validierung:

* doppelte Werte
* ungültige Statuswechsel
* nicht erlaubte Aktionen
* Konflikte mit bestehendem Systemzustand

Regeln:

* Technische Validierung darf am Request-DTO stattfinden.
* Fachliche Validierung gehört in Service, Use Case oder Domain-Logik.
* Controller enthalten keine komplexe Validierungslogik.

---

## Pagination

Listen-Endpunkte verwenden Pagination.

Request:

```http
GET /api/v1/equipment?page=0&size=20
```

Response:

```json
{
  "data": [],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5
}
```

Regeln:

* `page` beginnt bei `0`.
* `size` muss begrenzt werden.
* Große Listen nicht ungefiltert und unpaginiert zurückgeben.

---

## Filtering, Sorting und Search

Filtering:

```http
GET /api/v1/equipment?status=AVAILABLE
```

Sorting:

```http
GET /api/v1/equipment?sort=name,asc
```

Search:

```http
GET /api/v1/equipment?q=searchTerm
```

Regeln:

* Filter sind exakt.
* Search ist textbasiert oder unscharf.
* Sortierfelder müssen kontrolliert werden.
* Ungültige Filter oder Sortierfelder mit `400 Bad Request` ablehnen.

---

## Versionierung

Die Versionierung erfolgt über den URL-Pfad:

```http
/api/v1/...
```

Eine neue Version ist nötig bei:

* Entfernen von Feldern
* Umbenennen von Feldern
* Änderung von Feldtypen
* Änderung bestehender Semantik
* Änderung der URL-Struktur

Keine neue Version ist nötig bei:

* neuen optionalen Feldern
* neuen optionalen Query-Parametern
* neuen Endpoints
* internen Implementierungsänderungen ohne Contract-Änderung

---

## OpenAPI / Swagger

API-Änderungen sollen dokumentiert werden.

Dokumentiert werden mindestens:

* Pfad
* HTTP-Methode
* Request-Body
* Response-Body
* Statuscodes
* Fehlerfälle
* Beispiel-Requests
* Beispiel-Responses

Wenn Dokumentation und Implementierung voneinander abweichen, gilt das als Fehler.

---

## Checkliste

Vor Abschluss einer API-Änderung prüfen:

* [ ] Pfad beginnt mit `/api/v1/...`.
* [ ] URL beschreibt eine Ressource, keine Aktion.
* [ ] HTTP-Methode passt zur Operation.
* [ ] Statuscodes sind korrekt.
* [ ] `201 Created` setzt bei Erstellung möglichst einen `Location`-Header.
* [ ] `204 No Content` enthält keinen Response-Body.
* [ ] Request-DTO wird verwendet.
* [ ] Response-DTO wird verwendet.
* [ ] Keine Entity wird direkt exposed.
* [ ] Fehlerformat ist konsistent.
* [ ] Fehler enthalten keine internen Details.
* [ ] Listen-Endpunkte verwenden Pagination.
* [ ] Filtering, Sorting und Search sind kontrolliert.
* [ ] OpenAPI/Swagger ist aktualisiert, falls vorhanden.
* [ ] Erfolgs- und Fehlerfälle sind getestet.
