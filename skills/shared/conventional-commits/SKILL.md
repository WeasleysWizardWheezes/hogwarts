---
name: conventional-commits
description: Verwende diesen Skill wenn du Commit-Messages oder Branch-Namen formulierst
---

# Conventional Commits

Quelle: https://www.conventionalcommits.org/en/v1.0.0/

## Format

```
<type>[(<scope>)][!]: <beschreibung>

[optionaler body]

[optionale footer]
```

## Types

| Type | Bedeutung |
|------|-----------|
| `feat` | Neues Feature |
| `fix` | Bugfix |
| `docs` | Nur Dokumentation |
| `style` | Formatierung, kein Logik-Inhalt |
| `refactor` | Code-Änderung ohne neues Feature oder Fix |
| `test` | Tests hinzufügen oder anpassen |
| `chore` | Build, CI, Dependencies, Tooling |
| `perf` | Performance-Verbesserung |
| `revert` | Commit rückgängig machen |

## Scope

Optional. Beschreibt den betroffenen Bereich der Codebase in Klammern.

```
feat(equipment): ...
fix(inspection): ...
```

## Breaking Changes

Mit `!` nach Type/Scope oder als Footer:

```
feat(api)!: remove deprecated endpoint

BREAKING CHANGE: /api/v1/equipment ist nicht mehr verfügbar
```

## Beispiele

```
feat(equipment): add REST endpoint for status change
fix(inspection): correct date calculation for annual checks
test(equipment): add unit tests for QR code validation
docs(readme): update setup instructions
chore(ci): add PIT mutation testing step
refactor(vehicle): extract loading check into separate service
```

## Regeln

- Kleinbuchstaben außer `BREAKING CHANGE`
- Beschreibung im Imperativ ("add" nicht "added")
- Kein Punkt am Ende der Beschreibung
- Scope ist optional, aber hilfreich
- Erste Zeile max. 72 Zeichen

## Branch-Benennung

Branch-Namen orientieren sich am Commit-Type des Hauptänderung:

```
feat/<issue-nr>-<kurzbeschreibung>
fix/<issue-nr>-<kurzbeschreibung>
refactor/<issue-nr>-<kurzbeschreibung>
chore/<issue-nr>-<kurzbeschreibung>
```

Beispiele:
```
feat/42-add-equipment-status-endpoint
fix/17-correct-inspection-interval
```
