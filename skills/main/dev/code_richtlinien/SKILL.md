---

name: code-richtlinien
description: Nutze diesen Skill beim Schreiben, Ändern, Refactoren oder Reviewen von Projektcode, insbesondere bei Architektur, Package-Struktur, DDD, Schichtentrennung, DTO-Platzierung, Mappern, Repository-Grenzen, Tests, Formatierung und Namenskonventionen.
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

# Code-Richtlinien

Dieser Skill definiert projektweite Code-Regeln. Java-spezifische Regeln stehen im Java-Skill. Spring-spezifische Regeln stehen im Spring-Skill. API-Design-Regeln stehen im API-Guidelines-Skill.

## Grundprinzipien

* Code muss verständlich, testbar und langfristig wartbar sein.
* Architekturregeln haben Vorrang vor kurzfristiger Bequemlichkeit.
* Bestehende Projektkonventionen haben Vorrang.
* Fachlogik, API-Modelle und technische Infrastruktur müssen klar getrennt bleiben.

---

## Architektur

Verwendet wird DDD-orientierte Architektur mit Package-by-Feature.

Jedes Feature bekommt ein eigenes Package:

```text
de.projekt.feature.*
```

Innerhalb eines Features gilt folgende Struktur:

```text
feature/
  domain/
  application/
  infrastructure/
  api/
```

---

## Schichten

### `domain`

Enthält:

* Entities
* Value Objects
* Domain Events
* Domain Services
* fachliche Repository-Interfaces

Die Domain enthält keine Abhängigkeiten zu Spring, REST, JPA oder anderen technischen Frameworks.

### `application`

Enthält:

* Use Cases
* Application Services
* Orchestrierung von Abläufen
* fachliche Anwendungslogik

Die Application-Schicht verwendet die Domain, kennt aber keine REST-Details.

### `infrastructure`

Enthält:

* Persistenzimplementierungen
* externe Adapter
* technische Konfigurationen
* Implementierungen technischer Schnittstellen

Die Infrastruktur implementiert Interfaces aus `domain` oder `application`.

### `api`

Enthält:

* REST-Controller
* Request-DTOs
* Response-DTOs
* Mapper
* zentrale API-Fehlerabbildung

Die API-Schicht ruft Application Services auf. Application Services rufen Use Cases auf.

---

## Abhängigkeitsregeln

* Abhängigkeiten zeigen nach innen.
* `domain` hängt von keiner anderen Projektschicht ab.
* `application` darf `domain` verwenden.
* `api` darf `application` verwenden.
* `infrastructure` darf `domain` und `application` verwenden.
* `domain` kennt keine DTOs, Controller, Persistenzdetails oder Framework-Konfiguration.
* `application` kennt keine HTTP-Details.

---

## DTOs und Mapper

* Request- und Response-DTOs werden getrennt.
* DTOs liegen im `api`-Package des jeweiligen Features.
* Request-DTOs enthalten nur Felder, die der Client setzen darf.
* Response-DTOs enthalten nur Felder, die der Client sehen darf.
* Keine Persistenzobjekte direkt als API-Response zurückgeben.
* Keine API-DTOs in der Domain verwenden.
* Mapper übersetzen nur zwischen API-Modell und Application-/Domain-Modell.
* Mapper enthalten keine Fachlogik.

---

## Repository-Grenzen

* Repository-Interfaces sollen fachlich benannt sein.
* Fachliche Repository-Interfaces liegen in `domain` oder `application`.
* Technische Persistenzdetails gehören in `infrastructure`.
* Domain- oder Application-Code darf nicht von konkreten Persistenztechnologien abhängen.

Gut:

```java
public interface EquipmentRepository {
    Optional<Equipment> findById(EquipmentId equipmentId);
    Equipment save(Equipment equipment);
}
```

Schlecht in der Domain:

```java
public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {
}
```

---

## Tests

* Neue Fachlogik benötigt Unit-Tests.
* Neue oder geänderte Endpunkte benötigen passende API-, Controller- oder Integrationstests.
* Tests decken relevante Erfolgs- und Fehlerfälle ab.
* Domain-Logik wird bevorzugt ohne Framework-Kontext getestet.
* Mocks nur dort verwenden, wo echte Abhängigkeiten den Test unnötig schwer machen.
* Testmethoden dürfen sprechende Namen verwenden, auch wenn sie vom normalen Methodenschema abweichen.

Beispiel:

```java
@Test
void should_throw_when_equipment_does_not_exist() {
    // arrange
    // act
    // assert
}
```

---

## Formatierung

* Encoding: UTF-8
* Zeilenlänge: max. 120 Zeichen
* Einrückung: 4 Spaces, keine Tabs
* `case`-Blöcke: 4 Spaces
* Array-Initialisierung: 4 Spaces
* Line-Wrapping: 8 Spaces
* Geschweifte Klammern immer setzen, auch bei Einzeilern
* Öffnende Klammer `{` steht am Ende der Zeile
* Leerzeile zwischen Methoden und logischen Feldergruppen

## Whitespace

* Kein Whitespace vor `,`, `;`, `)`
* Kein Padding in Klammern: `method(arg)`, nicht `method( arg )`
* Generics ohne extra Spaces: `List<String>`, nicht `List <String>`

---

## Namenskonventionen

### Packages

Packages verwenden nur Kleinbuchstaben und keine Unterstriche.

Muster:

```text
^[a-z]+(\.[a-z][a-z0-9]*)*$
```

### Klassen und Interfaces

* PascalCase verwenden.
* Namen müssen fachlich klar sein.
* Keine bedeutungslosen Suffixe wie `Service2`, `NewService` oder `ManagerImpl`.

### Methoden

* camelCase verwenden.
* Mindestens 2 Zeichen.
* Buchstabe am Anfang.
* Methodenname beschreibt eine Aktion oder Abfrage.

Beispiele:

```java
findById
createEquipment
markAsUnavailable
```

### Felder, Variablen und Parameter

* camelCase verwenden.
* Mindestens 2 Zeichen.
* Keine Einzelbuchstaben-Variablen.

Nicht verwenden:

```java
i
x
e
o
```

Stattdessen:

```java
index
value
exception
equipment
```

Diese Regel gilt auch für Lambda-Parameter, Catch-Parameter und Schleifenvariablen.

---

## Checkliste

Vor Abschluss einer Code-Änderung prüfen:

* [ ] Package-Struktur folgt Package-by-Feature.
* [ ] Abhängigkeiten zeigen nach innen.
* [ ] Domain ist frei von Framework- und Persistenzabhängigkeiten.
* [ ] Application-Schicht kennt keine HTTP-Details.
* [ ] DTOs, Domain-Objekte und Persistenzobjekte sind sauber getrennt.
* [ ] Mapper enthalten keine Fachlogik.
* [ ] Repository-Grenzen sind sauber eingehalten.
* [ ] Tests decken relevante Erfolgs- und Fehlerfälle ab.
* [ ] Namenskonventionen werden eingehalten.
* [ ] Formatierung entspricht Checkstyle.
