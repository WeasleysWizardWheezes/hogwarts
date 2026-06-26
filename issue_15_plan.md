# Planung für Issue #15: Dienst-, Termin- & Einsatzplanung

## Akzeptanzkriterien

### 1. Dienst- und Übungsplanung
- [ ] Es soll möglich sein, Dienste und Übungen zu erstellen, zu bearbeiten und zu löschen.
- [ ] Dienste und Übungen sollen einem Kalender zugeordnet werden können.
- [ ] Dienste und Übungen sollen wiederkehrend sein können (täglich, wöchentlich, monatlich, jährlich).
- [ ] Dienste und Übungen sollen einer Organisationseinheit zugeordnet werden können.

### 2. Kalenderfunktionen
- [ ] Es soll möglich sein, Kalender zu erstellen, zu bearbeiten und zu löschen.
- [ ] Kalender sollen Organisationseinheiten zugeordnet werden können.
- [ ] Kalender sollen freigegeben werden können (öffentlich, privat, für bestimmte Benutzer).
- [ ] Kalender sollen in verschiedenen Ansichten dargestellt werden können (Tagesansicht, Wochenansicht, Monatsansicht, Jahresansicht).

### 3. Terminverwaltung für Organisationseinheiten
- [ ] Es soll möglich sein, Termine für Organisationseinheiten zu erstellen, zu bearbeiten und zu löschen.
- [ ] Termine sollen einem Kalender zugeordnet werden können.
- [ ] Termine sollen wiederkehrend sein können (täglich, wöchentlich, monatlich, jährlich).
- [ ] Termine sollen einer Organisationseinheit zugeordnet werden können.

### 4. Teilnahmeverwaltung (Zu-/Absagen)
- [ ] Es soll möglich sein, Teilnehmer zu einem Termin, Dienst oder einer Übung hinzuzufügen oder zu entfernen.
- [ ] Teilnehmer sollen ihre Teilnahme bestätigen oder absagen können.
- [ ] Es soll möglich sein, den Status der Teilnahme zu sehen (zugesagt, abgesagt, unentschieden).
- [ ] Es soll möglich sein, Erinnerungen an Teilnehmer zu senden.

### 5. Erinnerungsfunktionen
- [ ] Es soll möglich sein, Erinnerungen für Termine, Dienste und Übungen zu erstellen.
- [ ] Erinnerungen sollen per E-Mail, SMS oder Push-Benachrichtigung gesendet werden können.
- [ ] Erinnerungen sollen zu einem bestimmten Zeitpunkt vor dem Termin gesendet werden können (z. B. 1 Stunde, 1 Tag, 1 Woche).
- [ ] Erinnerungen sollen wiederkehrend sein können.

### 6. Alarm- bzw. Einsatzplanung über AAO/Strukturen
- [ ] Es soll möglich sein, Alarme und Einsätze zu erstellen, zu bearbeiten und zu löschen.
- [ ] Alarme und Einsätze sollen einer Organisationseinheit zugeordnet werden können.
- [ ] Alarme und Einsätze sollen einem Kalender zugeordnet werden können.
- [ ] Alarme und Einsätze sollen wiederkehrend sein können.

### 7. Automatische Termin- und Einsatzzuordnung
- [ ] Es soll möglich sein, Termine und Einsätze automatisch basierend auf bestimmten Kriterien zuzuordnen (z. B. Verfügbarkeit der Teilnehmer, Priorität des Termins).
- [ ] Es soll möglich sein, Regeln für die automatische Zuordnung zu erstellen und zu verwalten.
- [ ] Es soll möglich sein, die automatische Zuordnung manuell zu überschreiben.

## API-Spezifikation

### 1. Dienst- und Übungsplanung

#### Dienst erstellen
```http
POST /api/services
Content-Type: application/json

{
  "title": "String",
  "description": "String",
  "startDate": "ISO-8601",
  "endDate": "ISO-8601",
  "isRecurring": "Boolean",
  "recurrencePattern": "String",
  "organizationUnitId": "String"
}
```

#### Dienst bearbeiten
```http
PUT /api/services/{id}
Content-Type: application/json

{
  "title": "String",
  "description": "String",
  "startDate": "ISO-8601",
  "endDate": "ISO-8601",
  "isRecurring": "Boolean",
  "recurrencePattern": "String",
  "organizationUnitId": "String"
}
```

#### Dienst löschen
```http
DELETE /api/services/{id}
```

#### Übung erstellen
```http
POST /api/exercises
Content-Type: application/json

{
  "title": "String",
  "description": "String",
  "startDate": "ISO-8601",
  "endDate": "ISO-8601",
  "isRecurring": "Boolean",
  "recurrencePattern": "String",
  "organizationUnitId": "String"
}
```

#### Übung bearbeiten
```http
PUT /api/exercises/{id}
Content-Type: application/json

{
  "title": "String",
  "description": "String",
  "startDate": "ISO-8601",
  "endDate": "ISO-8601",
  "isRecurring": "Boolean",
  "recurrencePattern": "String",
  "organizationUnitId": "String"
}
```

#### Übung löschen
```http
DELETE /api/exercises/{id}
```

### 2. Kalenderfunktionen

#### Kalender erstellen
```http
POST /api/calendars
Content-Type: application/json

{
  "name": "String",
  "description": "String",
  "organizationUnitId": "String",
  "isPublic": "Boolean"
}
```

#### Kalender bearbeiten
```http
PUT /api/calendars/{id}
Content-Type: application/json

{
  "name": "String",
  "description": "String",
  "organizationUnitId": "String",
  "isPublic": "Boolean"
}
```

#### Kalender löschen
```http
DELETE /api/calendars/{id}
```

#### Kalender freigeben
```http
POST /api/calendars/{id}/share
Content-Type: application/json

{
  "userIds": ["String"],
  "permissions": ["String"]
}
```

### 3. Terminverwaltung für Organisationseinheiten

#### Termin erstellen
```http
POST /api/appointments
Content-Type: application/json

{
  "title": "String",
  "description": "String",
  "startDate": "ISO-8601",
  "endDate": "ISO-8601",
  "isRecurring": "Boolean",
  "recurrencePattern": "String",
  "organizationUnitId": "String",
  "calendarId": "String"
}
```

#### Termin bearbeiten
```http
PUT /api/appointments/{id}
Content-Type: application/json

{
  "title": "String",
  "description": "String",
  "startDate": "ISO-8601",
  "endDate": "ISO-8601",
  "isRecurring": "Boolean",
  "recurrencePattern": "String",
  "organizationUnitId": "String",
  "calendarId": "String"
}
```

#### Termin löschen
```http
DELETE /api/appointments/{id}
```

### 4. Teilnahmeverwaltung (Zu-/Absagen)

#### Teilnehmer hinzufügen
```http
POST /api/appointments/{id}/participants
Content-Type: application/json

{
  "userIds": ["String"]
}
```

#### Teilnehmer entfernen
```http
DELETE /api/appointments/{id}/participants
Content-Type: application/json

{
  "userIds": ["String"]
}
```

#### Teilnahme bestätigen
```http
POST /api/appointments/{id}/participants/{userId}/accept
```

#### Teilnahme absagen
```http
POST /api/appointments/{id}/participants/{userId}/decline
```

#### Teilnahme-Status abfragen
```http
GET /api/appointments/{id}/participants/{userId}/status
```

### 5. Erinnerungsfunktionen

#### Erinnerung erstellen
```http
POST /api/reminders
Content-Type: application/json

{
  "appointmentId": "String",
  "userId": "String",
  "timeBefore": "String",
  "method": "String",
  "isRecurring": "Boolean"
}
```

#### Erinnerung bearbeiten
```http
PUT /api/reminders/{id}
Content-Type: application/json

{
  "appointmentId": "String",
  "userId": "String",
  "timeBefore": "String",
  "method": "String",
  "isRecurring": "Boolean"
}
```

#### Erinnerung löschen
```http
DELETE /api/reminders/{id}
```

### 6. Alarm- bzw. Einsatzplanung über AAO/Strukturen

#### Alarm erstellen
```http
POST /api/alarms
Content-Type: application/json

{
  "title": "String",
  "description": "String",
  "startDate": "ISO-8601",
  "endDate": "ISO-8601",
  "organizationUnitId": "String",
  "calendarId": "String",
  "isRecurring": "Boolean",
  "recurrencePattern": "String"
}
```

#### Alarm bearbeiten
```http
PUT /api/alarms/{id}
Content-Type: application/json

{
  "title": "String",
  "description": "String",
  "startDate": "ISO-8601",
  "endDate": "ISO-8601",
  "organizationUnitId": "String",
  "calendarId": "String",
  "isRecurring": "Boolean",
  "recurrencePattern": "String"
}
```

#### Alarm löschen
```http
DELETE /api/alarms/{id}
```

### 7. Automatische Termin- und Einsatzzuordnung

#### Regel erstellen
```http
POST /api/rules
Content-Type: application/json

{
  "name": "String",
  "description": "String",
  "conditions": "String",
  "actions": "String"
}
```

#### Regel bearbeiten
```http
PUT /api/rules/{id}
Content-Type: application/json

{
  "name": "String",
  "description": "String",
  "conditions": "String",
  "actions": "String"
}
```

#### Regel löschen
```http
DELETE /api/rules/{id}
```

#### Automatische Zuordnung ausführen
```http
POST /api/auto-assign
Content-Type: application/json

{
  "appointmentId": "String",
  "ruleId": "String"
}
```

## Nächste Schritte

1. **Fachliches Review**: Die Akzeptanzkriterien und die API-Spezifikation müssen fachlich geprüft werden.
2. **Status aktualisieren**: Nach dem Review wird der Status auf "Ready Backend" gesetzt.
3. **Entwicklung**: Die Entwicklung der Backend-Funktionen kann beginnen.
