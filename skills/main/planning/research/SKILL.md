---
name: recherche
description: Verwende wenn vor einer Implementierungsentscheidung Informationen über Technologien, Libraries oder Ansätze gesammelt werden müssen
---

# Recherche

## Overview

Sammle gezielt Informationen bevor du Entscheidungen triffst. Dokumentiere Quellen, Alternativen und Begründung.

## When to Use

- Neue Technologie/Library evaluieren
- Architektur-Entscheidung steht an
- Best Practice für ein Problem unklar
- Fehler dessen Ursache nicht offensichtlich ist

## Vorgehen

1. **Frage formulieren:** Was genau muss geklärt werden?
2. **Quellen suchen:** Offizielle Docs > StackOverflow > Blog Posts
3. **Alternativen identifizieren:** Mindestens 2 Optionen
4. **Vergleichen:** Pro/Contra für jeden Ansatz
5. **Entscheidung dokumentieren:** Was, Warum, Was verworfen

## Output-Format

```markdown
## Recherche: [Frage]

### Kontext
Warum muss das geklärt werden?

### Optionen
| Option | Pro | Contra |
|--------|-----|--------|
| A | ... | ... |
| B | ... | ... |

### Entscheidung
[Option X] weil [Begründung].

### Quellen
- [Link/Referenz]
```

## Regeln

- Nie implementieren ohne vorher zu recherchieren wenn Unsicherheit besteht
- Zeitbox: Max. 30 Minuten pro Recherche, dann entscheiden
- Ergebnisse in Ticket oder Iterationsdoku festhalten
- Explizit machen was NICHT untersucht wurde
