---
name: fachliches-review
description: Verwende wenn Tickets, Pläne oder Implementierungen gegen fachliche Anforderungen und Domänenkorrektheit geprüft werden müssen
---

# Fachliches Review

## Overview

Prüfe ob Tickets, Pläne und Implementierungen fachlich korrekt sind – unabhängig von der technischen Qualität.

## When to Use

- Ticket wurde erstellt → fachliche Vollständigkeit prüfen
- Implementierung fertig → gegen Akzeptanzkriterien prüfen
- PR erstellt → fachliche Korrektheit vor Merge

## Checkliste

### Ticket-Review
- [ ] Beschreibung ist fachlich korrekt und vollständig
- [ ] Akzeptanzkriterien decken alle fachlichen Fälle ab
- [ ] Keine fachlichen Annahmen die nicht explizit gemacht sind
- [ ] Edge Cases aus der Domäne berücksichtigt

### Implementierungs-Review
- [ ] Geschäftsregeln korrekt umgesetzt
- [ ] Fachliche Begriffe korrekt im Code (Ubiquitous Language)
- [ ] Alle Akzeptanzkriterien erfüllt
- [ ] Keine ungewollten Seiteneffekte auf andere fachliche Abläufe

## Regeln

- Fachliches Review nach dem technischen Review
- Bei fachlichen Zweifeln: Rückfrage stellen, nicht durchwinken
- Ergebnis dokumentieren: Approved / Changes Requested + Begründung
