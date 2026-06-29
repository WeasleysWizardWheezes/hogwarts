---
name: ticketerstellung
description: Verwende wenn ein Feature oder eine User Story in implementierbare GitHub Issues mit klaren Akzeptanzkriterien zerlegt werden muss
---

# Ticket-Erstellung

## Overview

Zerlege Anforderungen in kleine, unabhängig implementierbare Issues. Jedes Ticket hat ein klares Ziel, Akzeptanzkriterien und eine Testbarkeit.

## When to Use

- Neues Feature aus Product Backlog wird in Sprint gezogen
- User Story muss in technische Tasks zerlegt werden
- Backlog Refinement

## Ticket-Struktur

```markdown
## Titel
[Verb] + [Was] + [Kontext]
Beispiel: "Implementiere REST-Endpoint für User-Registrierung"

## Beschreibung
Was soll erreicht werden? Kurzer fachlicher Kontext.

## Akzeptanzkriterien
- [ ] [Testbares Kriterium 1]
- [ ] [Testbares Kriterium 2]
- [ ] Tests geschrieben und grün
- [ ] Code-Review bestanden

## Technische Hinweise
- Betroffene Dateien/Module
- Abhängigkeiten zu anderen Tickets
- Relevante Patterns/Architektur-Regeln
```

## Regeln

- Ein Ticket = eine logische Einheit (max. halber Arbeitstag)
- Jedes Akzeptanzkriterium muss automatisiert testbar sein
- Keine Ticket-Ketten: Jedes Ticket ist eigenständig implementierbar
- Abhängigkeiten explizit als "blocked by #X" dokumentieren
- Ein Ticket muss dem Status Ready Frontend oder Ready Backend zugewisen werden

## Verification

- Ticket ist verständlich ohne mündliche Erklärung
- Akzeptanzkriterien sind als Tests formulierbar
- Umfang ist in einer Session abarbeitbar
