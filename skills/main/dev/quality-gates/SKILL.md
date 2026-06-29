---
name: quality-gates
description: Verwende wenn du prüfen musst welche Qualitätsschwellen Code erfüllen muss bevor er gemergt oder released werden darf
---

# Quality Gates

## Code Coverage (JaCoCo)
- Minimum 80% Line Coverage (BUNDLE-Level)
- Phase: verify

## Mutation Testing (PITest)
- Mutation Threshold: 70%
- Phase: manuell (`mvn pitest:mutationCoverage`)

## Code Style (Checkstyle)
- Muss vor Commit grün sein
- failsOnError: true
- Phase: verify
