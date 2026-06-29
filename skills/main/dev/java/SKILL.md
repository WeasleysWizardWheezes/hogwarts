---
name: java
description: Nutze diesen Skill beim Schreiben, Ändern, Refactoren oder Reviewen von Java-Code, insbesondere bei Sprachfeatures, Immutability, Null-Handling, Exceptions, Logging, Imports und Java-spezifischen Best Practices.
---

# Java

Dieser Skill definiert Java-spezifische Regeln. Architektur, Package-Struktur, Spring-Konventionen und API-Design gehören nicht hierher

## Sprachfeatures

* Verwende Java-25-Sprach- und Plattformfeatures, wenn sie die Lösung klarer oder robuster machen.
* Verwende Records für einfache Datencontainer, DTOs und Value Objects.
* Verwende Sealed Classes oder Sealed Interfaces, wenn eine begrenzte Menge erlaubter Untertypen fachlich sinnvoll ist.
* Verwende Pattern Matching, wenn dadurch Typprüfungen lesbarer werden.
* Verwende Virtual Threads für blockierende I/O-lastige Abläufe, wenn das Projekt und die Infrastruktur dafür geeignet sind.
* Verwende moderne Java-APIs bevorzugt gegenüber veralteten oder unnötig komplexen Konstrukten.

## Immutability

* Bevorzuge unveränderliche Objekte.
* Felder sollen möglichst `final` sein.
* Records sind für unveränderliche Datenstrukturen zu bevorzugen.
* Collections, die nach außen gegeben werden, dürfen nicht unbeabsichtigt veränderbar sein.
* Mutable State muss bewusst begründet sein.

## Null-Handling

* Gib kein `null` zurück, wenn `Optional` fachlich sinnvoll ist.
* Verwende `Optional` primär als Rückgabewert, nicht als Feldtyp oder Parameter.
* Prüfe externe Eingaben explizit.
* Vermeide unnötige Null-Zustände durch sinnvolle Konstruktoren, Records und Validierung.

## Typinferenz mit `var`

* Verwende `var` nur bei lokalen Variablen mit offensichtlich erkennbarem Typ.
* Verwende keinen `var`, wenn dadurch Lesbarkeit oder Verständnis verschlechtert wird.
* Verwende keinen `var` für komplexe generische Rückgaben, bei denen der konkrete Typ relevant ist.

Gut:

```java
var equipment = repository.findById(equipmentId);
var names = List.of("A", "B");
```

Schlecht:

```java
var result = service.process(input);
```

Wenn der Typ nicht klar ist, explizit schreiben.

## Exceptions

* Für fachliche Fehler eigene Exception-Klassen verwenden.
* Generische Exceptions wie `RuntimeException` nicht direkt für fachliche Fehler werfen.
* Standardexceptions wie `IllegalArgumentException` sind für einfache defensive Programmierfehler erlaubt.
* Fachliche Exceptions sollen einen maschinenlesbaren Error-Code tragen.
* Checked Exceptions nur verwenden, wenn der Aufrufer realistisch darauf reagieren kann oder eine externe API sie erzwingt.
* Domain-Fehler werden als RuntimeExceptions modelliert.
* API- oder Framework-Schichten übersetzen Exceptions in passende technische Antworten.
* Catch-Blöcke dürfen nicht leer sein.
* Gefangene Exceptions müssen behandelt, geloggt oder weitergeworfen werden.

Beispiel:

```java
public class ResourceNotFoundException extends RuntimeException {

    private final String errorCode;

    public ResourceNotFoundException(String message) {
        super(message);
        this.errorCode = "RESOURCE_NOT_FOUND";
    }

    public String getErrorCode() {
        return errorCode;
    }
}
```

## Logging

* Verwende einen Logger statt `System.out.println`.
* Logge keine Passwörter, Tokens, Secrets oder sensiblen Daten.
* Exception-Objekte werden beim Logging als letztes Argument übergeben.

Gut:

```java
log.error("Could not load resource with id {}", resourceId, exception);
```

Schlecht:

```java
log.error("Could not load resource: " + exception.getMessage());
```

## Imports

* Keine Wildcard-Imports verwenden.
* Immer explizite Imports verwenden.
* Ungenutzte Imports entfernen.
* Statische Imports sparsam verwenden.
* Statische Imports sind erlaubt, wenn sie die Lesbarkeit verbessern, zum Beispiel bei Assertions.

Import-Reihenfolge:

```text
java.*
javax.*
jakarta.*
org.*
com.*
de.projekt.*
```

Zwischen Importgruppen eine Leerzeile setzen.
