---
name: issue-workflow
description: Verwende diesen Skill, wenn du mit dem GitHub Project Board interagierst – Issues übernehmen, zwischen Spalten verschieben, zuweisen und Arbeit übergeben
---

# Issue-Workflow

## Board-Spalten

| Spalte | Bedeutung |
|--------|-----------|
| Backlog | Rohe Anforderung, nicht ausgearbeitet |
| Ready Frontend | Spezifiziertes Frontend-Ticket mit Akzeptanzkriterien |
| Ready Backend | Spezifiziertes Backend-Ticket mit Akzeptanzkriterien |
| In Development | Du arbeitest aktiv daran |
| Blocked | Du kommst nicht weiter |
| Needs Review | PR erstellt, Pipeline grün, wartet auf fachliches Review |
| In Review | Fachliches Review läuft |
| Done | Gemergt in main |

## Reihenfolge der Arbeit

```
1. Planen:   Backlog → Ready Backend / Ready Frontend
2. Entwickeln:    Ready Backend/Frontend → In Development → Needs Review
3. Reviewen: Needs Review → In Review → Done (oder zurück)
```

Du führst alle drei Schritte aus – auch am selben Issue hintereinander. Wenn du ein Ticket spezifiziert, implementiert und die Pipeline grün ist, kannst du es direkt selbst reviewen.

## Schritt 1: Planen (Backlog → Ready)

1. Issue aus "Backlog" wählen
2. Domänenwissen anwenden
3. Bei Unklarheiten recherchieren
4. Akzeptanzkriterien + API-Spec schreiben
5. Fachliches Ticket-Review durchführen
6. Entscheidung:
    - Genehmigt → Merge → Issue nach "Ready Backend" oder "Ready Frontend" verschieben
    - Änderungen nötig → Wiederhole Planung ab Schritt 2 und beziehe dabei die Erkenntnisse aus dem Review mit ein

## Schritt 2: Entwickeln (Ready → Needs Review)

1. Issue aus "Ready Backend" oder "Ready Frontend" wählen (ohne Assignee)
2. Assignee auf dich setzen
3. Issue nach "In Development" verschieben
4. **Issue-Kommentare lesen** – falls das Issue bereits bearbeitet wurde, stehen dort Hinweise aus vorherigen Reviews
5. Branch erstellen (oder bestehenden Branch nutzen bei Nacharbeit)
6. Code + Tests schreiben (Review-Kommentare berücksichtigen)
7. Committen und pushen → Pipeline abwarten
8. Bei Pipeline-Fehler: Report lesen, fixen, erneut pushen (max. 5 Iterationen)
9. Bei Pipeline-Erfolg: PR erstellen (`Closes #<nr>`)
10. Issue nach "Needs Review" verschieben
11. Assignee entfernen (damit ein anderer Agent reviewen kann)

## Schritt 3: Reviewen (Needs Review → Done oder zurück)

Voraussetzung: Pipeline ist bereits grün (technisches Review durch Quality Gates bestanden).

1. Issue aus "Needs Review" wählen (dein eigenes oder ein fremdes ohne Assignee)
2. Assignee auf dich setzen (falls nicht schon du)
3. Issue nach "In Review" verschieben
4. Fachliches Implementierungs-Review durchführen
5. Entscheidung:
   - Genehmigt → Merge → Issue nach "Done"
   - Änderungen nötig → Kommentar was falsch ist → Issue zurück nach "Ready Backend"/"Ready Frontend" → Assignee entfernen

## Regeln

### Issue übernehmen

Mehrere Agent-Instanzen laufen gleichzeitig auf verschiedenen Rechnern. Um Konflikte zu vermeiden:

1. Board abfragen: Issues ohne Assignee im gewünschten Status filtern
2. Ein Issue auswählen
3. **Sofort** Assignee auf dich setzen
4. **3–5 Sekunden warten**
5. Issue erneut lesen und Assignee prüfen:
   - Assignee = du → Weiterarbeiten
   - Assignee = jemand anderes → Ein anderer Agent war schneller. Nächstes Issue versuchen

### Während der Arbeit

- Nur 1 Issue gleichzeitig
- Nur wer als Assignee eingetragen ist, darf am Issue arbeiten
- Bei Nacharbeit: gleicher Branch, kein neuer

### Blockade

Wenn du nach 5 Pipeline-Iterationen nicht weiterkommst:
1. Issue nach "Blocked" verschieben
2. Kommentar am Issue: Was ist das Problem? Was wurde versucht?
3. Nächstes freies Issue übernehmen

### Issue abgeben

- Assignee entfernen, wenn das Issue für den nächsten Schritt bereit ist
- Oder Assignee auf den nächsten Bearbeiter setzen
