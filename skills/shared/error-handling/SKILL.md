---
name: error-handling
description: Verwende diesen Skill wenn ein Befehl oder Schritt fehlschlägt und du einen Fehlerbericht erstellen musst
---

# Fehlerbehandlung

## Wann anwenden

Immer wenn ein Befehl mit einem Fehler endet (Exit Code ≠ 0, Fehlermeldung, unerwartetes Ergebnis).

## Vorgehen

1. **STOPP** – Nicht weitermachen, nicht den gleichen Befehl wiederholen
2. Fehlerbericht erstellen (siehe Format unten)
3. Auf Anweisung warten

## Fehlerbericht-Format

```
## Fehlerbericht

### Kontext
[Welcher Workflow-Schritt? Welches Issue? Was war das Ziel?]

### Was wurde versucht?
[Exakter Befehl oder Aktion]

### Fehlermeldung
[Exakte Ausgabe – vollständig zitieren, nicht zusammenfassen]

### Analyse
- Wahrscheinliche Ursache:
- Betroffene Komponente (AGENTS.md / Skill / Pipeline / Infrastruktur):
- Ist der Fehler reproduzierbar?

### Lösungsvorschläge
- [ ] [Vorschlag 1 + was konkret geändert werden muss]
- [ ] [Vorschlag 2 + Alternative falls Vorschlag 1 nicht möglich]

### Empfehlung
[Welcher Vorschlag ist am sinnvollsten und warum?]
```

## Regeln

- Nie einen fehlgeschlagenen Befehl unverändert wiederholen
- Immer die **exakte** Fehlermeldung im Bericht zitieren
- Mindestens 2 Lösungsvorschläge nennen
- Wenn der Fehler auf ein Problem in AGENTS.md oder einem Skill hindeutet: explizit benennen was geändert werden sollte