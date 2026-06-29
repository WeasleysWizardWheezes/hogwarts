---
name: github-cli
description: GitHub-Operationen über die gh CLI – Issues, Projekte, Pull Requests, Repos, Branches und CI/CD Pipelines
---

# GitHub CLI Skill

## Defaults

| Konstante | Wert |
|-----------|------|
| `ORG` | `WeasleysWizardWheezes` |
| `REPO` | `WeasleysWizardWheezes/hogwarts` |
| `PROJECT_NUMBER` | `1` |
| `PROJECT_ID` | `PVT_kwDOEY8mYs4Ba8yv` |
| `PROJECT_URL` | `https://github.com/orgs/WeasleysWizardWheezes/projects/1` |
| `DEFAULT_BRANCH` | `main` |
| `STATUS_FIELD_ID` | `PVTSSF_lADOEY8mYs4Ba8yvzhVwu5s` |

### Status-Optionen (single-select-option-id)

| Status | Option-ID |
|--------|-----------|
| Backlog | `f75ad846` |
| Ready Frontend | `61e4505c` |
| Ready Backend | `d764878c` |
| In development | `47fc9ee4` |
| Blocked | `388c510c` |
| Needs review | `142ae9ca` |
| In review | `df73e18b` |
| Done | `98236657` |

Verwende diese Werte direkt – nie nachfragen oder ermitteln.

## Regeln

1. Immer `--format json` anhängen (maschinenlesbare Ausgabe)
2. Befehle direkt ausführen, keine explorativen Calls
3. Bei Listen immer `-L 100` für ausreichende Ergebnisse
4. Issue-/PR-URLs: `https://github.com/WeasleysWizardWheezes/hogwarts/issues/<NR>`
5. **NIE direkt auf main pushen** – immer Feature-Branch + PR

## Container-Umgebung

- Repo ist NICHT vorhanden → erst klonen
- `gh auth` ist vorkonfiguriert → `gh repo clone` setzt Auth korrekt
- **Nach dem Clone IMMER `gh auth setup-git` ausführen** damit git push funktioniert.
- **Setup gebündelt (1 Call):**
  ```sh
  cd /tmp && gh repo clone WeasleysWizardWheezes/hogwarts && cd hogwarts && gh auth setup-git && git config user.email "bot@openclaw.dev" && git config user.name "OpenClaw Bot" && chmod +x mvnw && git checkout -b <branch-name>
  ```

---

## 1. Repository & Code

### Repo klonen
```sh
gh repo clone WeasleysWizardWheezes/hogwarts
```

### Datei remote lesen (ohne Clone)
```sh
gh repo read-file <path> --repo WeasleysWizardWheezes/hogwarts --branch main
```

### Verzeichnis remote auflisten
```sh
gh repo read-dir <path> --repo WeasleysWizardWheezes/hogwarts --branch main
```

---

## 2. Branches & Git

### Feature-Branch erstellen
```sh
git checkout main
git pull origin main
git checkout -b <branch-name>
```

### Änderungen committen & pushen
```sh
git add .
git commit -m "<commit-message>"
git push -u origin <branch-name>
```

### Branch aktualisieren (main integrieren)
```sh
git fetch origin
git merge origin/main
```

### Auf existierenden Branch wechseln
```sh
git checkout <branch-name>
git pull origin <branch-name>
```

---

## 3. Pull Requests

### PR erstellen
```sh
gh pr create --repo WeasleysWizardWheezes/hogwarts --title "<title>" --body "<body>" --base main
```

### PRs auflisten
```sh
gh pr list --repo WeasleysWizardWheezes/hogwarts --format json
```

### PR Details anzeigen
```sh
gh pr view <number> --repo WeasleysWizardWheezes/hogwarts --json title,body,state,reviewDecision,mergeable,headRefName
```

### PR Review (genehmigen)
```sh
gh pr review <number> --repo WeasleysWizardWheezes/hogwarts --approve
```

### PR Review (Änderungen anfordern)
```sh
gh pr review <number> --repo WeasleysWizardWheezes/hogwarts --request-changes --body "<kommentar>"
```

### PR mergen
```sh
gh pr merge <number> --repo WeasleysWizardWheezes/hogwarts --merge
```

### PR Checks prüfen
```sh
gh pr checks <number> --repo WeasleysWizardWheezes/hogwarts --format json
```

---

## 4. Issues / Tickets

### Alle Issues auflisten
```sh
gh issue list --repo WeasleysWizardWheezes/hogwarts --json number,title,state,labels,assignees -L 100
```

### Issue Details anzeigen
⚠️ `gh issue view` hat KEIN `--format` Flag! Verwende `--json`:
```sh
gh issue view <number> --repo WeasleysWizardWheezes/hogwarts --json title,body,state,labels,assignees,comments
```

### Issue erstellen
```sh
gh issue create --repo WeasleysWizardWheezes/hogwarts --title "<title>" --body "<body>"
```

### Issue Beschreibung bearbeiten
```sh
gh issue edit <number> --repo WeasleysWizardWheezes/hogwarts --body "<neue-beschreibung>"
```

### Issue Titel bearbeiten
```sh
gh issue edit <number> --repo WeasleysWizardWheezes/hogwarts --title "<neuer-titel>"
```

### Issue Labels setzen
```sh
gh issue edit <number> --repo WeasleysWizardWheezes/hogwarts --add-label "<label>"
```

### Issue Assignee setzen
```sh
gh issue edit <number> --repo WeasleysWizardWheezes/hogwarts --add-assignee <username>
```

### Issue Kommentar anhängen
```sh
gh issue comment <number> --repo WeasleysWizardWheezes/hogwarts --body "<kommentar>"
```

### Issue schließen
```sh
gh issue close <number> --repo WeasleysWizardWheezes/hogwarts
```

### Branch für Issue erstellen (verlinkt Issue automatisch)
```sh
gh issue develop <number> --repo WeasleysWizardWheezes/hogwarts --name <branch-name> --checkout
```

---

## 5. Project Board

⚠️ **Der Ticket-Status wird AUSSCHLIESSLICH über das Project Board verwaltet – NIEMALS über `gh issue list` mit Labels!**

### Alle Tickets im Board auflisten (EINSTIEGSPUNKT für "neues Ticket im Backlog")
```sh
gh project item-list 1 --owner WeasleysWizardWheezes --format json -L 100
```
→ Im Output: `status`-Feld zeigt die Board-Spalte (Backlog, In development, Done, etc.)

### Tickets nach Status filtern
```sh
gh project item-list 1 --owner WeasleysWizardWheezes --format json -L 100 | jq '[.items[] | select(.status == "<StatusName>")]'
```

### Board-Felder und Status-Optionen ermitteln

Normalerweise nicht nötig – alle IDs stehen oben in der Defaults-Tabelle. Nur bei Änderungen am Board:

```sh
gh project field-list 1 --owner WeasleysWizardWheezes --format json
```

### Ticket-Status ändern (verschieben)

Die IDs stehen oben in der Defaults-Tabelle. Direkt verwenden:

```sh
gh project item-edit --id <item-id> --project-id PVT_kwDOEY8mYs4Ba8yv --field-id PVTSSF_lADOEY8mYs4Ba8yvzhVwu5s --single-select-option-id <option-id-aus-tabelle>
```

Beispiel – Ticket nach "Done":
```sh
gh project item-edit --id PVTI_xxx --project-id PVT_kwDOEY8mYs4Ba8yv --field-id PVTSSF_lADOEY8mYs4Ba8yvzhVwu5s --single-select-option-id 98236657
```

### Issue zum Board hinzufügen
```sh
gh project item-add 1 --owner WeasleysWizardWheezes --url https://github.com/WeasleysWizardWheezes/hogwarts/issues/<NR> --format json
```

### Draft-Issue im Board erstellen
```sh
gh project item-create 1 --owner WeasleysWizardWheezes --title "<title>" --body "<body>" --format json
```

### Item archivieren
```sh
gh project item-archive 1 --owner WeasleysWizardWheezes --id <item-id>
```

### ID-Referenz

⚠️ `--project-id`, `--field-id` und `--single-select-option-id` stehen alle oben in der Defaults-Tabelle. Nur die `--id` (Item-ID, Format `PVTI_...`) muss aus dem `item-list` Output gelesen werden.

---

## 6. CI/CD Pipelines

### Letzte Runs auflisten
```sh
gh run list --repo WeasleysWizardWheezes/hogwarts --format json -L 10
```

### Runs eines Branches
```sh
gh run list --repo WeasleysWizardWheezes/hogwarts --branch <branch-name> --format json
```

### Run-Details
```sh
gh run view <run-id> --repo WeasleysWizardWheezes/hogwarts --json status,conclusion,jobs
```

### Fehlgeschlagene Logs lesen
```sh
gh run view <run-id> --repo WeasleysWizardWheezes/hogwarts --log-failed
```

### Run erneut starten
```sh
gh run rerun <run-id> --repo WeasleysWizardWheezes/hogwarts
```

### Ergebnisse interpretieren
- `conclusion: "success"` → Pipeline grün
- `conclusion: "failure"` → `--log-failed` lesen, Fehler beheben

---

## 7. Häufige Workflows

### Workflow: Triviales Ticket (Ziel: 6 Calls)

```sh
# 1. Board abfragen
gh project item-list 1 --owner WeasleysWizardWheezes --format json -L 100

# 2. Repo klonen + Setup + Branch (EIN Call)
cd /tmp && gh repo clone WeasleysWizardWheezes/hogwarts && cd hogwarts && gh auth setup-git && git config user.email "bot@openclaw.dev" && git config user.name "OpenClaw Bot" && chmod +x mvnw && git checkout -b fix/<NR>-beschreibung

# 3. Änderung durchführen
rm -f src/test/java/path/to/File.java

# 4. Commit + Push (EIN Call)
cd /tmp/hogwarts && git add . && git commit -m "fix: beschreibung (closes #<NR>)" && git push -u origin fix/<NR>-beschreibung

# 5. PR erstellen
gh pr create --repo WeasleysWizardWheezes/hogwarts --title "fix: beschreibung" --body "Closes #<NR>" --base main

# 6. Status auf Done setzen (IDs aus Defaults-Tabelle oben!)
gh project item-edit --id <item-id> --project-id PVT_kwDOEY8mYs4Ba8yv --field-id PVTSSF_lADOEY8mYs4Ba8yvzhVwu5s --single-select-option-id 98236657
```

### Workflow: Komplexes Ticket (Feature)

```sh
# 1. Board abfragen
gh project item-list 1 --owner WeasleysWizardWheezes --format json -L 100

# 2. Repo klonen + Setup + Branch (EIN Call)
cd /tmp && gh repo clone WeasleysWizardWheezes/hogwarts && cd hogwarts && gh auth setup-git && git config user.email "bot@openclaw.dev" && git config user.name "OpenClaw Bot" && chmod +x mvnw && git checkout -b feature/<NR>-beschreibung

# 3. Code + Tests schreiben (mehrere write-Calls)
# ... Entity, Repository, Service, Controller, Tests ...

# 4. Lokal testen (Java ist im Container verfügbar)
cd /tmp/hogwarts && chmod +x mvnw && ./mvnw clean verify

# 5. Commit + Push (EIN Call)
cd /tmp/hogwarts && git add . && git commit -m "feat: beschreibung (closes #<NR>)" && git push -u origin feature/<NR>-beschreibung

# 5. PR erstellen
gh pr create --repo WeasleysWizardWheezes/hogwarts --title "feat: beschreibung" --body "Closes #<NR>" --base main

# 6. Pipeline abwarten und prüfen
gh pr checks <NR> --repo WeasleysWizardWheezes/hogwarts --format json

# 7. Bei Fehler: Logs lesen, fixen, erneut pushen
gh run view <run-id> --repo WeasleysWizardWheezes/hogwarts --log-failed

# 8. Status aktualisieren
gh project item-edit --id <item-id> --project-id PVT_kwDOEY8mYs4Ba8yv --field-id PVTSSF_lADOEY8mYs4Ba8yvzhVwu5s --single-select-option-id <option-id>
```
